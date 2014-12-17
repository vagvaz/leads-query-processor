package leads.tajo.catalog;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.catalog.CatalogUtil;
import org.apache.tajo.catalog.FunctionDesc;
import org.apache.tajo.catalog.exception.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.catalog.proto.CatalogProtos.IndexDescProto;
import org.apache.tajo.catalog.store.CatalogStore;
import org.infinispan.Cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.tajo.catalog.proto.CatalogProtos.AlterTablespaceProto.AlterTablespaceType;
import static org.apache.tajo.catalog.proto.CatalogProtos.TablespaceProto;

/**
 * CatalogServer guarantees that all operations are thread-safe.
 * So, we don't need to consider concurrency problem here.
 */
public class LeadsMemStore implements CatalogStore {
    private  Map<String, String> tablespaces = null;
    private  Map<String, String> databases  = null ;
    private  Map<String, CatalogProtos.FunctionDescProto> functions = null;
//    private  Map<String, Map<String, IndexDescProto>> indexes = null ;
    private  Map<String, String> indexes = null ;
    private  Map<String, String> indexesByColumn = null ;
//    private  Map<String, Map<String, IndexDescProto>> indexesByColumn = null ;
    private InfinispanManager manager;

    public LeadsMemStore(Configuration conf) throws Exception {
        LQPConfiguration.initialize();
        boolean isManagerStarted = false;
        int count = 0;
        while(!isManagerStarted) {

            try {
                manager = InfinispanClusterSingleton.getInstance().getManager();
                isManagerStarted = manager.isStarted();
                if (count > 2) {
                    System.err.println("Exiting we could not start LeadsMemStore for CatalogServer so we exit");
                    throw new Exception("Could not start InfinispanManager");
                }
            }catch(Exception e ){
                isManagerStarted = false;
                System.err.println("Failed Starting LeadsMemStore Manager retrying for " +  ++count);
                throw new Exception("Problem with getting maps for Leads Memstore " + e.getMessage());

            }

        }
//        try {
            tablespaces = manager.getPersisentCache("leads.processor.catalog.tablespaces");
            databases = manager.getPersisentCache("leads.processor.catalog.databases");
            functions = manager.getPersisentCache("leads.processor.catalog.functions");
            indexes = manager.getPersisentCache("leads.processor.catalog.indexes");
            indexesByColumn = manager.getPersisentCache("leads.processor.catalog.indexesByColumn");
//        }
//        catch(Exception e){
//            System.err.println("Problem with starting the Catalog maps exiting...\n" + e.getMessage());
//            System.exit(-1);
//        }
    }


    public void close() throws IOException {
//        databases.clear();
//        functions.clear();
//        indexes.clear();
        manager.removePersistentCache("leads.processor.catalog.tablespaces");
        manager.removePersistentCache("leads.processor.catalog.databases");
        manager.removePersistentCache("leads.processor.catalog.functions");
        manager.removePersistentCache("leads.processor.catalog.indexes");
        manager.removePersistentCache("leads.processor.catalog.indexesByColumn");
    }

    @Override
    public void createTablespace(String spaceName, String spaceUri) throws CatalogException {
        Object tableSpace = tablespaces.get(spaceName);
        if (tableSpace != null) {
            throw new AlreadyExistsTablespaceException(spaceName);
        }

        tablespaces.put(spaceName, spaceUri);
    }

    @Override
    public boolean existTablespace(String spaceName) throws CatalogException {
//        return tablespaces.containsKey(spaceName);
        Object tableSpace = tablespaces.get(spaceName);
        return tableSpace != null;
    }

    @Override
    public void dropTablespace(String spaceName) throws CatalogException {
        Object tableSpace = tablespaces.get(spaceName);
        if (tableSpace == null) {
            throw new NoSuchTablespaceException(spaceName);
        }
        tablespaces.remove(spaceName);
    }

    @Override
    public Collection<String> getAllTablespaceNames() throws CatalogException {
        return tablespaces.keySet();
    }

    @Override
    public TablespaceProto getTablespace(String spaceName) throws CatalogException {
        Object tableSpace = tablespaces.get(spaceName);
        if (tableSpace == null) {
            throw new NoSuchTablespaceException(spaceName);
        }

        TablespaceProto.Builder builder = TablespaceProto.newBuilder();
        builder.setSpaceName(spaceName);
        builder.setUri(tablespaces.get(spaceName));
        return builder.build();
    }

    @Override
    public void alterTablespace(CatalogProtos.AlterTablespaceProto alterProto)
        throws CatalogException {
        Object tableSpace = tablespaces.get(alterProto.getSpaceName());
        if (tableSpace == null) {
            throw new NoSuchTablespaceException(alterProto.getSpaceName());
        }

        if (alterProto.getCommandList().size() > 0) {
            for (CatalogProtos.AlterTablespaceProto.AlterTablespaceCommand cmd : alterProto
                                                                                     .getCommandList()) {
                if (cmd.getType() == AlterTablespaceType.LOCATION) {
                    CatalogProtos.AlterTablespaceProto.SetLocation setLocation = cmd.getLocation();
                    tablespaces.put(alterProto.getSpaceName(), setLocation.getUri());
                }
            }
        }
    }

    @Override
    public void createDatabase(String databaseName, String tablespaceName) throws CatalogException {
        Object database = databases.get(databaseName);
        if (database != null) {
            throw new AlreadyExistsDatabaseException(databaseName);
        }
        Cache newDb = (Cache) manager.getPersisentCache("leads.processor.databases.sub."+databaseName);
        databases.put(databaseName, newDb.getName());
    }

    @Override
    public boolean existDatabase(String databaseName) throws CatalogException {
        Object database = databases.get(databaseName);
        return (database != null);
    }

    @Override
    public void dropDatabase(String databaseName) throws CatalogException {
        Object database = databases.get(databaseName);
        if (database == null) {
            throw new NoSuchDatabaseException(databaseName);
        }
        manager.removePersistentCache((String) database);
        databases.remove(databaseName);
    }

    @Override
    public Collection<String> getAllDatabaseNames() throws CatalogException {
        return databases.keySet();
    }

    /**
     * Get a database namespace from a Map instance.
     */
    private <T> Map<String, T> checkAndGetDatabaseNS(final Map<String, String> databaseMap,
                                                        String databaseName) {
        Object databaseN = databases.get(databaseName);
        if (databaseN != null) {
//            return databaseMap.get(databaseName);
            return manager.getPersisentCache((String) databaseN);
        } else {
            throw new NoSuchDatabaseException(databaseName);
        }
    }

    @Override
    public void createTable(CatalogProtos.TableDescProto request) throws CatalogException {
        String[] splitted = CatalogUtil.splitTableName(request.getTableName());
        if (splitted.length == 1) {
            throw new IllegalArgumentException("createTable() requires a qualified table name, but it is \""
                                                   + request.getTableName() + "\".");
        }
        String databaseName = splitted[0];
        String tableName = splitted[1];

        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);

        String tbName = tableName;
        Object table = database.get(tbName);
        if (table != null) {
            throw new AlreadyExistsTableException(tbName);
        }
        database.put(tbName, request);
    }

    @Override
    public boolean existTable(String dbName, String tbName) throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, dbName);
        Object table = database.get(tbName);
        return (table != null);
//        return database.containsKey(tbName);
    }

    @Override
    public void dropTable(String dbName, String tbName) throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, dbName);

        Object table = database.get(tbName);
        if (table != null) {
            manager.removePersistentCache(dbName+"."+tbName);
            database.remove(tbName);
        } else {
            throw new NoSuchTableException(tbName);
        }
    }

    /* (non-Javadoc)
     * @see CatalogStore#alterTable(AlterTableDesc)
     */
    @Override
    public void alterTable(CatalogProtos.AlterTableDescProto alterTableDescProto)
        throws CatalogException {

        String[] split = CatalogUtil.splitTableName(alterTableDescProto.getTableName());
        if (split.length == 1) {
            throw new IllegalArgumentException("alterTable() requires a qualified table name, but it is \""
                                                   + alterTableDescProto.getTableName() + "\".");
        }
        String databaseName = split[0];
        String tableName = split[1];

        final Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);

        final CatalogProtos.TableDescProto tableDescProto = database.get(tableName);
        CatalogProtos.TableDescProto newTableDescProto;
        CatalogProtos.SchemaProto schemaProto;

        switch (alterTableDescProto.getAlterTableType()) {
            case RENAME_TABLE:
                Object table = database.get(alterTableDescProto.getNewTableName());
                if (table == null) {
                    throw new AlreadyExistsTableException(alterTableDescProto.getNewTableName());
                }
                // Currently, we only use the default table space (i.e., WAREHOUSE directory).
                String spaceUri = tablespaces.get(TajoConstants.DEFAULT_TABLESPACE_NAME);
                // Create a new table directory.
                String newPath = new Path(spaceUri, new Path(databaseName, alterTableDescProto
                                                                               .getNewTableName()))
                                     .toString();
                newTableDescProto = tableDescProto.toBuilder()
                                        .setTableName(alterTableDescProto.getNewTableName())
                                        .setPath(newPath).build();
                database.remove(tableName);
                database.put(alterTableDescProto.getNewTableName(), newTableDescProto);
                break;
            case RENAME_COLUMN:
                schemaProto = tableDescProto.getSchema();
                final int index = getIndexOfColumnToBeRenamed(schemaProto.getFieldsList(),
                                                                 alterTableDescProto
                                                                     .getAlterColumnName()
                                                                     .getOldColumnName());
                final CatalogProtos.ColumnProto columnProto = schemaProto.getFields(index);
                final CatalogProtos.ColumnProto newcolumnProto =
                    columnProto.toBuilder()
                        .setName(alterTableDescProto.getAlterColumnName().getNewColumnName())
                        .build();
                newTableDescProto = tableDescProto.toBuilder().setSchema(schemaProto.toBuilder().
                                                                                                    setFields(index,
                                                                                                                 newcolumnProto)
                                                                             .build()).build();
                database.put(tableName, newTableDescProto);
                break;
            case ADD_COLUMN:
                schemaProto = tableDescProto.getSchema();
                CatalogProtos.SchemaProto newSchemaProto =
                    schemaProto.toBuilder().addFields(alterTableDescProto.getAddColumn()).build();
                newTableDescProto = tableDescProto.toBuilder().setSchema(newSchemaProto).build();
                database.put(tableName, newTableDescProto);
                break;
            default:
                //TODO
        }
    }

    @Override
    public void updateTableStats(CatalogProtos.UpdateTableStatsProto request) throws CatalogException {
        String [] splitted = CatalogUtil.splitTableName(request.getTableName());
        if (splitted.length == 1) {
            throw new IllegalArgumentException("createTable() requires a qualified table name, but it is \""
                    + request.getTableName() + "\".");
        }
        String databaseName = splitted[0];
        String tableName = splitted[1];

        final Map<String, CatalogProtos.TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);
        final CatalogProtos.TableDescProto tableDescProto = database.get(tableName);
        CatalogProtos.TableDescProto newTableDescProto = tableDescProto.toBuilder().setStats(request
                .getStats().toBuilder()).build();
        database.put(tableName, newTableDescProto);
    }


    private int getIndexOfColumnToBeRenamed(List<CatalogProtos.ColumnProto> fieldList,
                                               String columnName) {
        int fieldCount = fieldList.size();
        for (int index = 0; index < fieldCount; index++) {
            CatalogProtos.ColumnProto columnProto = fieldList.get(index);
            if (null != columnProto && columnProto.getName().equalsIgnoreCase(columnName)) {
                return index;
            }
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see CatalogStore#getTable(java.lang.String)
     */
    @Override
    public CatalogProtos.TableDescProto getTable(String databaseName, String tableName)
        throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);

        Object table = database.get(tableName);
        if (table != null) {
            CatalogProtos.TableDescProto unqualified = database.get(tableName);
            CatalogProtos.TableDescProto.Builder builder =
                CatalogProtos.TableDescProto.newBuilder();
            CatalogProtos.SchemaProto schemaProto =
                CatalogUtil
                    .getQualfiedSchema(databaseName + "." + tableName, unqualified.getSchema());
            builder.mergeFrom(unqualified);
            builder.setSchema(schemaProto);
            return builder.build();
        } else {
            throw new NoSuchTableException(tableName);
        }
    }

    /* (non-Javadoc)
     * @see CatalogStore#getAllTableNames()
     */
    @Override
    public List<String> getAllTableNames(String databaseName) throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);
        return new ArrayList<String>(database.keySet());
    }

    @Override
    public void addPartitionMethod(CatalogProtos.PartitionMethodProto partitionMethodProto)
        throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public CatalogProtos.PartitionMethodProto getPartitionMethod(String databaseName,
                                                                    String tableName)
        throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);

        if (database.containsKey(tableName)) {
            CatalogProtos.TableDescProto table = database.get(tableName);
            return table.hasPartition() ? table.getPartition() : null;
        } else {
            throw new NoSuchTableException(tableName);
        }
    }

    @Override
    public boolean existPartitionMethod(String databaseName, String tableName)
        throws CatalogException {
        Map<String, CatalogProtos.TableDescProto> database =
            checkAndGetDatabaseNS(databases, databaseName);

        if (database.containsKey(tableName)) {
            CatalogProtos.TableDescProto table = database.get(tableName);
            return table.hasPartition();
        } else {
            throw new NoSuchTableException(tableName);
        }
    }

    @Override
    public void dropPartitionMethod(String databaseName, String tableName) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public void addPartitions(CatalogProtos.PartitionsProto partitionDescList)
        throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public void addPartition(String databaseName, String tableName, CatalogProtos.PartitionDescProto
                                                                        partitionDescProto)
        throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public CatalogProtos.PartitionsProto getPartitions(String tableName) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public CatalogProtos.PartitionDescProto getPartition(String partitionName)
        throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public void delPartition(String partitionName) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
    public void dropPartitions(String tableName) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    /* (non-Javadoc)
     * @see CatalogStore#createIndex(nta.catalog.proto.CatalogProtos.IndexDescProto)
     */
    @Override
    public void createIndex(IndexDescProto proto) throws CatalogException {
        final String databaseName = proto.getTableIdentifier().getDatabaseName();

        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        Map<String, IndexDescProto> indexByColumn =
            checkAndGetDatabaseNS(indexesByColumn, databaseName);

        if (index.containsKey(proto.getIndexName())) {
            throw new AlreadyExistsIndexException(proto.getIndexName());
        }

        index.put(proto.getIndexName(), proto);
        indexByColumn.put(proto.getTableIdentifier().getTableName() + "."
                              + CatalogUtil.extractSimpleName(proto.getColumn().getName()), proto);
    }

    /* (non-Javadoc)
     * @see CatalogStore#dropIndex(java.lang.String)
     */
    @Override
    public void dropIndex(String databaseName, String indexName) throws CatalogException {
        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        if (!index.containsKey(indexName)) {
            throw new NoSuchIndexException(indexName);
        }
        index.remove(indexName);
    }

    /* (non-Javadoc)
     * @see CatalogStore#getIndexByName(java.lang.String)
     */
    @Override
    public IndexDescProto getIndexByName(String databaseName, String indexName)
        throws CatalogException {
        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        if (!index.containsKey(indexName)) {
            throw new NoSuchIndexException(indexName);
        }

        return index.get(indexName);
    }

    /* (non-Javadoc)
     * @see CatalogStore#getIndexByName(java.lang.String, java.lang.String)
     */
    @Override
    public IndexDescProto getIndexByColumn(String databaseName, String tableName, String columnName)
        throws CatalogException {

        Map<String, IndexDescProto> indexByColumn =
            checkAndGetDatabaseNS(indexesByColumn, databaseName);
        if (!indexByColumn.containsKey(columnName)) {
            throw new NoSuchIndexException(columnName);
        }

        return indexByColumn.get(columnName);
    }

    @Override
    public boolean existIndexByName(String databaseName, String indexName) throws CatalogException {
        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        return index.containsKey(indexName);
    }

    @Override
    public boolean existIndexByColumn(String databaseName, String tableName, String columnName)
        throws CatalogException {
        Map<String, IndexDescProto> indexByColumn =
            checkAndGetDatabaseNS(indexesByColumn, databaseName);
        return indexByColumn.containsKey(columnName);
    }

    @Override
    public IndexDescProto[] getIndexes(String databaseName, String tableName)
        throws CatalogException {
        List<IndexDescProto> protos = new ArrayList<IndexDescProto>();
        Map<String, IndexDescProto> indexByColumn =
            checkAndGetDatabaseNS(indexesByColumn, databaseName);
        for (IndexDescProto proto : indexByColumn.values()) {
            if (proto.getTableIdentifier().getTableName().equals(tableName)) {
                protos.add(proto);
            }
        }

        return protos.toArray(new IndexDescProto[protos.size()]);
    }

    @Override
    public void addFunction(FunctionDesc func) throws CatalogException {
        // to be implemented
    }

    @Override
    public void deleteFunction(FunctionDesc func) throws CatalogException {
        // to be implemented
    }

    @Override
    public void existFunction(FunctionDesc func) throws CatalogException {
        // to be implemented
    }

    @Override
    public List<String> getAllFunctionNames() throws CatalogException {
        // to be implemented
        return null;
    }

}
