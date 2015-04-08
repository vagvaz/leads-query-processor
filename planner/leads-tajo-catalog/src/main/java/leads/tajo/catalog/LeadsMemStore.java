package leads.tajo.catalog;

/**
 * Created by tr on 15/12/2014.
 */

import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.catalog.CatalogUtil;
import org.apache.tajo.catalog.FunctionDesc;
import org.apache.tajo.catalog.Schema;
import org.apache.tajo.catalog.exception.*;
import org.apache.tajo.catalog.proto.CatalogProtos.*;
import org.apache.tajo.catalog.store.CatalogStore;
import org.apache.tajo.rpc.protocolrecords.PrimitiveProtos.KeyValueProto;
import org.apache.tajo.util.TUtil;

import java.io.IOException;
import java.util.*;

import static org.apache.tajo.catalog.proto.CatalogProtos.AlterTablespaceProto.AlterTablespaceType;

/**
 * CatalogServer guarantees that all operations are thread-safe.
 * So, we don't need to consider concurrency problem here.
 */
public class LeadsMemStore implements CatalogStore {
    private final Map<String, String> tablespaces = Maps.newHashMap();
  private final Map<String, Map<String, TableDescProto>> databases = Maps.newHashMap();
  private final Map<String, FunctionDescProto> functions = Maps.newHashMap();
    private final Map<String, Map<String, IndexDescProto>> indexes = Maps.newHashMap();
    private final Map<String, Map<String, IndexDescProto>> indexesByColumn = Maps.newHashMap();

    public LeadsMemStore(Configuration conf) {
    }


    public void close() throws IOException {
        databases.clear();
        functions.clear();
        indexes.clear();
    }

    @Override
    public void createTablespace(String spaceName, String spaceUri) throws CatalogException {
        if (tablespaces.containsKey(spaceName)) {
            throw new AlreadyExistsTablespaceException(spaceName);
        }

        tablespaces.put(spaceName, spaceUri);
    }

    @Override
    public boolean existTablespace(String spaceName) throws CatalogException {
        return tablespaces.containsKey(spaceName);
    }

    @Override
    public void dropTablespace(String spaceName) throws CatalogException {
        if (!tablespaces.containsKey(spaceName)) {
            throw new NoSuchTablespaceException(spaceName);
        }
        tablespaces.remove(spaceName);
    }

    @Override
    public Collection<String> getAllTablespaceNames() throws CatalogException {
        return tablespaces.keySet();
    }

    @Override
    public List<TablespaceProto> getTablespaces() throws CatalogException {
        List<TablespaceProto> tablespaceList = TUtil.newList();
        int tablespaceId = 0;

        for (String spaceName: tablespaces.keySet()) {
            TablespaceProto.Builder builder = TablespaceProto.newBuilder();
            builder.setSpaceName(spaceName);
            builder.setUri(tablespaces.get(spaceName));
            builder.setId(tablespaceId++);
            tablespaceList.add(builder.build());
        }

        return tablespaceList;
    }

    @Override
    public TablespaceProto getTablespace(String spaceName) throws CatalogException {
        if (!tablespaces.containsKey(spaceName)) {
            throw new NoSuchTablespaceException(spaceName);
        }

        TablespaceProto.Builder builder = TablespaceProto.newBuilder();
        builder.setSpaceName(spaceName);
        builder.setUri(tablespaces.get(spaceName));
        return builder.build();
    }

    @Override
  public void alterTablespace(AlterTablespaceProto alterProto) throws CatalogException {
        if (!tablespaces.containsKey(alterProto.getSpaceName())) {
            throw new NoSuchTablespaceException(alterProto.getSpaceName());
        }

        if (alterProto.getCommandList().size() > 0) {
      for (AlterTablespaceProto.AlterTablespaceCommand cmd : alterProto.getCommandList()) {
                if(cmd.getType() == AlterTablespaceType.LOCATION) {
          AlterTablespaceProto.SetLocation setLocation = cmd.getLocation();
                    tablespaces.put(alterProto.getSpaceName(), setLocation.getUri());
                }
            }
        }
    }

    @Override
    public void createDatabase(String databaseName, String tablespaceName) throws CatalogException {
        if (databases.containsKey(databaseName)) {
            throw new AlreadyExistsDatabaseException(databaseName);
        }

    databases.put(databaseName, new HashMap<String, TableDescProto>());
    indexes.put(databaseName, new HashMap<String, IndexDescProto>());
    indexesByColumn.put(databaseName, new HashMap<String, IndexDescProto>());
    }

    @Override
    public boolean existDatabase(String databaseName) throws CatalogException {
        return databases.containsKey(databaseName);
    }

    @Override
    public void dropDatabase(String databaseName) throws CatalogException {
        if (!databases.containsKey(databaseName)) {
            throw new NoSuchDatabaseException(databaseName);
        }
        databases.remove(databaseName);
    indexes.remove(databaseName);
    indexesByColumn.remove(databaseName);
    }

    @Override
    public Collection<String> getAllDatabaseNames() throws CatalogException {
        return databases.keySet();
    }

    @Override
  public List<DatabaseProto> getAllDatabases() throws CatalogException {
    List<DatabaseProto> databaseList = new ArrayList<DatabaseProto>();
        int dbId = 0;

        for (String databaseName: databases.keySet()) {
      DatabaseProto.Builder builder = DatabaseProto.newBuilder();

            builder.setId(dbId++);
            builder.setName(databaseName);
            builder.setSpaceId(0);

            databaseList.add(builder.build());
        }

        return databaseList;
    }

    /**
     * Get a database namespace from a Map instance.
     */
    private <T> Map<String, T> checkAndGetDatabaseNS(final Map<String, Map<String, T>> databaseMap,
                                                     String databaseName) {
        if (databaseMap.containsKey(databaseName)) {
            return databaseMap.get(databaseName);
        } else {
            throw new NoSuchDatabaseException(databaseName);
        }
    }

    @Override
  public void createTable(TableDescProto request) throws CatalogException {
        String [] splitted = CatalogUtil.splitTableName(request.getTableName());
        if (splitted.length == 1) {
            throw new IllegalArgumentException("createTable() requires a qualified table name, but it is \""
                    + request.getTableName() + "\".");
        }
        String databaseName = splitted[0];
        String tableName = splitted[1];

    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);

        String tbName = tableName;
        if (database.containsKey(tbName)) {
            throw new AlreadyExistsTableException(tbName);
        }
        database.put(tbName, request);
    }

    @Override
  public void updateTableStats(UpdateTableStatsProto request) throws CatalogException {
        String [] splitted = CatalogUtil.splitTableName(request.getTableName());
        if (splitted.length == 1) {
            throw new IllegalArgumentException("createTable() requires a qualified table name, but it is \""
                    + request.getTableName() + "\".");
        }
        String databaseName = splitted[0];
        String tableName = splitted[1];

    final Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);
    final TableDescProto tableDescProto = database.get(tableName);
    TableDescProto newTableDescProto = tableDescProto.toBuilder().setStats(request
      .getStats().toBuilder()).build();
        database.put(tableName, newTableDescProto);
    }

    @Override
    public boolean existTable(String dbName, String tbName) throws CatalogException {
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, dbName);

        return database.containsKey(tbName);
    }

    @Override
    public void dropTable(String dbName, String tbName) throws CatalogException {
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, dbName);

        if (database.containsKey(tbName)) {
            database.remove(tbName);
        } else {
            throw new NoSuchTableException(tbName);
        }
    }

    /* (non-Javadoc)
     * @see CatalogStore#alterTable(AlterTableDesc)
     */
    @Override
  public void alterTable(AlterTableDescProto alterTableDescProto) throws CatalogException {

        String[] split = CatalogUtil.splitTableName(alterTableDescProto.getTableName());
        if (split.length == 1) {
            throw new IllegalArgumentException("alterTable() requires a qualified table name, but it is \""
                    + alterTableDescProto.getTableName() + "\".");
        }
        String databaseName = split[0];
        String tableName = split[1];

    final Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);

    final TableDescProto tableDescProto = database.get(tableName);
    TableDescProto newTableDescProto;
    SchemaProto schemaProto;

        switch (alterTableDescProto.getAlterTableType()) {
            case RENAME_TABLE:
                if (database.containsKey(alterTableDescProto.getNewTableName())) {
                    throw new AlreadyExistsTableException(alterTableDescProto.getNewTableName());
                }
                // Currently, we only use the default table space (i.e., WAREHOUSE directory).
                String spaceUri = tablespaces.get(TajoConstants.DEFAULT_TABLESPACE_NAME);
                // Create a new table directory.
                String newPath = new Path(spaceUri, new Path(databaseName, alterTableDescProto.getNewTableName())).toString();
                newTableDescProto = tableDescProto.toBuilder()
                        .setTableName(alterTableDescProto.getNewTableName())
                        .setPath(newPath).build();
                database.remove(tableName);
                database.put(alterTableDescProto.getNewTableName(), newTableDescProto);
                break;
            case RENAME_COLUMN:
                schemaProto = tableDescProto.getSchema();
                final int index = getIndexOfColumnToBeRenamed(schemaProto.getFieldsList(),
                        alterTableDescProto.getAlterColumnName().getOldColumnName());
        final ColumnProto columnProto = schemaProto.getFields(index);
        final ColumnProto newcolumnProto =
                        columnProto.toBuilder().setName(alterTableDescProto.getAlterColumnName().getNewColumnName()).build();
                newTableDescProto = tableDescProto.toBuilder().setSchema(schemaProto.toBuilder().
                        setFields(index, newcolumnProto).build()).build();
                database.put(tableName, newTableDescProto);
                break;
            case ADD_COLUMN:
                schemaProto = tableDescProto.getSchema();
        SchemaProto newSchemaProto =
                        schemaProto.toBuilder().addFields(alterTableDescProto.getAddColumn()).build();
                newTableDescProto = tableDescProto.toBuilder().setSchema(newSchemaProto).build();
                database.put(tableName, newTableDescProto);
                break;
            default:
                //TODO
        }
    }

  private int getIndexOfColumnToBeRenamed(List<ColumnProto> fieldList, String columnName) {
        int fieldCount = fieldList.size();
        for (int index = 0; index < fieldCount; index++) {
      ColumnProto columnProto = fieldList.get(index);
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
  public TableDescProto getTable(String databaseName, String tableName)
            throws CatalogException {
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);

        if (database.containsKey(tableName)) {
      TableDescProto unqualified = database.get(tableName);
      TableDescProto.Builder builder = TableDescProto.newBuilder();
      SchemaProto schemaProto =
                    CatalogUtil.getQualfiedSchema(databaseName + "." + tableName, unqualified.getSchema());
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
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);
        return new ArrayList<String>(database.keySet());
    }

    @Override
  public List<TableDescriptorProto> getAllTables() throws CatalogException {
    List<TableDescriptorProto> tableList = new ArrayList<TableDescriptorProto>();
        int dbId = 0, tableId = 0;

        for (String databaseName: databases.keySet()) {
      Map<String, TableDescProto> tables = databases.get(databaseName);
            List<String> tableNameList = TUtil.newList(tables.keySet());
            Collections.sort(tableNameList);

            for (String tableName: tableNameList) {
        TableDescProto tableDesc = tables.get(tableName);
        TableDescriptorProto.Builder builder = TableDescriptorProto.newBuilder();

                builder.setDbId(dbId);
                builder.setTid(tableId);
                builder.setName(tableName);
                builder.setPath(tableDesc.getPath());
                builder.setTableType(tableDesc.getIsExternal()?"EXTERNAL":"BASE");
                builder.setStoreType(CatalogUtil.getStoreTypeString(tableDesc.getMeta().getStoreType()));

                tableList.add(builder.build());
                tableId++;
            }
            dbId++;
        }

        return tableList;
    }

    @Override
  public List<TableOptionProto> getAllTableOptions() throws CatalogException {
    List<TableOptionProto> optionList = new ArrayList<TableOptionProto>();
        int tid = 0;

        for (String databaseName: databases.keySet()) {
      Map<String, TableDescProto> tables = databases.get(databaseName);
            List<String> tableNameList = TUtil.newList(tables.keySet());
            Collections.sort(tableNameList);

            for (String tableName: tableNameList) {
        TableDescProto table = tables.get(tableName);
        List<KeyValueProto> keyValueList = table.getMeta().getParams().getKeyvalList();

        for (KeyValueProto keyValue: keyValueList) {
          TableOptionProto.Builder builder = TableOptionProto.newBuilder();

                    builder.setTid(tid);
                    builder.setKeyval(keyValue);

                    optionList.add(builder.build());
                }
            }
            tid++;
        }

        return optionList;
    }

    @Override
  public List<TableStatsProto> getAllTableStats() throws CatalogException {
    List<TableStatsProto> statList = new ArrayList<TableStatsProto>();
        int tid = 0;

        for (String databaseName: databases.keySet()) {
      Map<String, TableDescProto> tables = databases.get(databaseName);
            List<String> tableNameList = TUtil.newList(tables.keySet());
            Collections.sort(tableNameList);

            for (String tableName: tableNameList) {
        TableDescProto table = tables.get(tableName);
        TableStatsProto.Builder builder = TableStatsProto.newBuilder();

                builder.setTid(tid);
                builder.setNumRows(table.getStats().getNumRows());
                builder.setNumBytes(table.getStats().getNumBytes());

                statList.add(builder.build());
            }
            tid++;
        }

        return statList;
    }

    @Override
  public List<ColumnProto> getAllColumns() throws CatalogException {
    List<ColumnProto> columnList = new ArrayList<ColumnProto>();
        int tid = 0;

        for (String databaseName: databases.keySet()) {
      Map<String, TableDescProto> tables = databases.get(databaseName);
            List<String> tableNameList = TUtil.newList(tables.keySet());
            Collections.sort(tableNameList);

            for (String tableName: tableNameList) {
        TableDescProto tableDesc = tables.get(tableName);

        for (ColumnProto column: tableDesc.getSchema().getFieldsList()) {
          ColumnProto.Builder builder = ColumnProto.newBuilder();
                    builder.setTid(tid);
                    builder.setName(column.getName());
                    builder.setDataType(column.getDataType());
                    columnList.add(builder.build());
                }
            }
            tid++;
        }

        return columnList;
    }

    @Override
  public void addPartitionMethod(PartitionMethodProto partitionMethodProto) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
  public PartitionMethodProto getPartitionMethod(String databaseName, String tableName)
            throws CatalogException {
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);

        if (database.containsKey(tableName)) {
      TableDescProto table = database.get(tableName);
            return table.hasPartition() ? table.getPartition() : null;
        } else {
            throw new NoSuchTableException(tableName);
        }
    }

    @Override
    public boolean existPartitionMethod(String databaseName, String tableName)
            throws CatalogException {
    Map<String, TableDescProto> database = checkAndGetDatabaseNS(databases, databaseName);

        if (database.containsKey(tableName)) {
      TableDescProto table = database.get(tableName);
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
  public void addPartitions(PartitionsProto partitionDescList) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
  public void addPartition(String databaseName, String tableName, PartitionDescProto
            partitionDescProto) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
  public PartitionsProto getPartitions(String tableName) throws CatalogException {
        throw new RuntimeException("not supported!");
    }

    @Override
  public PartitionDescProto getPartition(String partitionName) throws CatalogException {
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

    @Override
  public List<TablePartitionProto> getAllPartitions() throws CatalogException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see CatalogStore#createIndex(nta.catalog.proto.CatalogProtos.IndexDescProto)
     */
    @Override
    public void createIndex(IndexDescProto proto) throws CatalogException {
        final String databaseName = proto.getTableIdentifier().getDatabaseName();
    final String tableName = CatalogUtil.extractSimpleName(proto.getTableIdentifier().getTableName());

        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
    TableDescProto tableDescProto = getTable(databaseName, tableName);

        if (index.containsKey(proto.getIndexName())) {
            throw new AlreadyExistsIndexException(proto.getIndexName());
        }

        index.put(proto.getIndexName(), proto);
    String originalTableName = proto.getTableIdentifier().getTableName();
    String simpleTableName = CatalogUtil.extractSimpleName(originalTableName);
    indexByColumn.put(CatalogUtil.buildFQName(proto.getTableIdentifier().getDatabaseName(),
                    simpleTableName,
                    getUnifiedNameForIndexByColumn(proto)),
            proto);
    }

    /* (non-Javadoc)
     * @see CatalogStore#dropIndex(java.lang.String)
     */
    @Override
    public void dropIndex(String databaseName, String indexName) throws CatalogException {
        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
    Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
        if (!index.containsKey(indexName)) {
            throw new NoSuchIndexException(indexName);
        }
    IndexDescProto proto = index.get(indexName);
    final String tableName = CatalogUtil.extractSimpleName(proto.getTableIdentifier().getTableName());
    TableDescProto tableDescProto = getTable(databaseName, tableName);
        index.remove(indexName);
    String originalTableName = proto.getTableIdentifier().getTableName();
    String simpleTableName = CatalogUtil.extractSimpleName(originalTableName);
    indexByColumn.remove(CatalogUtil.buildFQName(proto.getTableIdentifier().getDatabaseName(),
        simpleTableName,
        getUnifiedNameForIndexByColumn(proto)));
    }

    /* (non-Javadoc)
     * @see CatalogStore#getIndexByName(java.lang.String)
     */
    @Override
    public IndexDescProto getIndexByName(String databaseName, String indexName) throws CatalogException {
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

        Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
        if (!indexByColumn.containsKey(columnName)) {
            throw new NoSuchIndexException(columnName);
        }

        return indexByColumn.get(columnName);
    }

  public IndexDescProto getIndexByColumns(String databaseName, String tableName, String[] columnNames) throws CatalogException {
    Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
    String simpleTableName = CatalogUtil.extractSimpleName(tableName);
    TableDescProto tableDescProto = getTable(databaseName, simpleTableName);
    String qualifiedColumnName = CatalogUtil.buildFQName(databaseName, simpleTableName,
            CatalogUtil.getUnifiedSimpleColumnName(new Schema(tableDescProto.getSchema()), columnNames));
    if (!indexByColumn.containsKey(qualifiedColumnName)) {
      throw new NoSuchIndexException(qualifiedColumnName);
    }

    return indexByColumn.get(qualifiedColumnName);
  }

    @Override
    public boolean existIndexByName(String databaseName, String indexName) throws CatalogException {
        Map<String, IndexDescProto> index = checkAndGetDatabaseNS(indexes, databaseName);
        return index.containsKey(indexName);
    }

    @Override
    public boolean existIndexByColumn(String databaseName, String tableName, String columnName)
            throws CatalogException {
        Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
        return indexByColumn.containsKey(columnName);
    }

  public boolean existIndexByColumns(String databaseName, String tableName, String[] columnNames) throws CatalogException {
    Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
    TableDescProto tableDescProto = getTable(databaseName, tableName);
    return indexByColumn.containsKey(
        CatalogUtil.buildFQName(databaseName, CatalogUtil.extractSimpleName(tableName),
            CatalogUtil.getUnifiedSimpleColumnName(new Schema(tableDescProto.getSchema()), columnNames)));
  }

  public List<String> getAllIndexNamesByTable(String databaseName, String tableName) throws CatalogException {
    List<String> indexNames = new ArrayList<String>();
    Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
    String simpleTableName = CatalogUtil.extractSimpleName(tableName);
    for (IndexDescProto proto : indexByColumn.values()) {
      if (proto.getTableIdentifier().getTableName().equals(simpleTableName)) {
        indexNames.add(proto.getIndexName());
      }
    }

    return indexNames;
  }

  public boolean existIndexesByTable(String databaseName, String tableName) throws CatalogException {
    Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
    String simpleTableName = CatalogUtil.extractSimpleName(tableName);
    for (IndexDescProto proto : indexByColumn.values()) {
      if (proto.getTableIdentifier().getTableName().equals(simpleTableName)) {
        return true;
      }
    }
    return false;
  }
    @Override
    public IndexDescProto[] getIndexes(String databaseName, String tableName) throws CatalogException {
        List<IndexDescProto> protos = new ArrayList<IndexDescProto>();
        Map<String, IndexDescProto> indexByColumn = checkAndGetDatabaseNS(indexesByColumn, databaseName);
        for (IndexDescProto proto : indexByColumn.values()) {
            if (proto.getTableIdentifier().getTableName().equals(tableName)) {
                protos.add(proto);
            }
        }

        return protos.toArray(new IndexDescProto[protos.size()]);
    }

    @Override
    public List<IndexDescProto> getAllIndexes() throws CatalogException {
        List<IndexDescProto> indexDescProtos = TUtil.newList();
        for (Map<String,IndexDescProto> indexMap : indexes.values()) {
            indexDescProtos.addAll(indexMap.values());
        }
        return indexDescProtos;
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

  public static String getUnifiedNameForIndexByColumn(IndexDescProto proto) {
    StringBuilder sb = new StringBuilder();
//    for (CatalogProtos.SortSpecProto columnSpec : proto.getKeySortSpecsList()) {
//      String[] identifiers = columnSpec.getColumn().getName().split(CatalogConstants.IDENTIFIER_DELIMITER_REGEXP);
//      sb.append(identifiers[identifiers.length-1]).append("_");
//    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }
}
