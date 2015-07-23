package eu.leads.processor.common.utils.storage;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.hadoop.fs.FileSystem.getDefaultUri;

/**
 * Created by vagvaz on 2/9/15.
 */
public class HDFSStorage implements LeadsStorage {
    private static FileSystem fileSystem;
    private static org.apache.hadoop.conf.Configuration hdfsConfiguration;
    private Logger log = LoggerFactory.getLogger(HDFSStorage.class);
    private Path basePath;
    private Properties storageConfiguration;
    UserGroupInformation ugi;

    @Override
    public boolean initialize(Properties configuration) {
        boolean result = false;
        setConfiguration(configuration);
        if (configuration.containsKey("prefix")) {
            basePath = new Path(configuration.getProperty("prefix"));
        } else {
            basePath = new Path("/user/leads/processor/");
        }
        result = initializeReader(configuration);
        ugi = UserGroupInformation.createRemoteUser(configuration.getProperty("hdfs.user"));

        if (!result)
            return result;
        result = initializeWriter(configuration);
        return result;
    }

    @Override
    public Properties getConfiguration() {
        return storageConfiguration;
    }

    @Override
    public void setConfiguration(Properties configuration) {
        this.storageConfiguration = configuration;
    }

    @Override
    public String getStorageType() {
        return HDFSStorage.class.toString();
    }

    @Override
    public boolean delete(String s) {
        Path newFolderPath= new Path(s);
        try {
            if(fileSystem.exists(newFolderPath))
               if(fileSystem.delete(newFolderPath, true)) {//Delete existing Directory
                   log.info("Deleted existing folder " + s);
                   System.out.println("Deleted existing folder " + s);
                   return true;
               }
                else
                log.error("Unable to delete folder: " + s);
            else
            log.info("Deleted existing folder "+ s);

        } catch (IOException e) {
            log.error("Unable to delete folder: " + s);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean initializeReader(Properties configuration) {
        try {
            hdfsConfiguration = new org.apache.hadoop.conf.Configuration(false);
            hdfsConfiguration.set("fs.defaultFS", configuration.getProperty("hdfs.url"));
            hdfsConfiguration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            hdfsConfiguration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());


            hdfsConfiguration.set("hdfs.url", configuration.getProperty("hdfs.url"));//"hdfs://snf-618466.vm.okeanos.grnet.gr:8020");
            //hdfsConfiguration.set("prefix", "/user/vagvaz/");

            hdfsConfiguration.set("prefix", configuration.getProperty("prefix"));
            //fileSystem = FileSystem.get(hdfsConfiguration);
            fileSystem = FileSystem.get(getDefaultUri(hdfsConfiguration), hdfsConfiguration, configuration.getProperty("hdfs.user"));


            if (!fileSystem.exists(basePath)) {
                log.info("Creating base path on HDFS " + configuration.getProperty("hdfs.url"));
                fileSystem.mkdirs(basePath);
            }

        } catch (Exception e) {
            log.error("Could not create HDFS remote FileSystem using \n" + configuration.toString() + "\n");
            return false;
        }
        return true;
    }

    @Override
    public byte[] read(final String uri) {
        try {
            return ugi.doAs(new PrivilegedExceptionAction<byte[]>() {
                @Override
                public byte[] run() throws IOException {

                    byte[] result;
                    Path path = new Path(uri);
                    SequenceFile.Reader reader = new SequenceFile.Reader(hdfsConfiguration, SequenceFile.Reader.file(path));


                    //            SequenceFile.Reader reader = new SequenceFile.Reader(hdfsConfiguration);
                    HDFSByteChunk chunk = new HDFSByteChunk();
                    //            reader.getCurrentValue(chunk);

                    IntWritable key = new IntWritable();
                    reader.next(key, chunk);

                    result = chunk.getData();

                    return result;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public List<byte[]> batchRead(List<String> uris) {
        List<byte[]> result = new ArrayList<>(uris.size());
        for (String uri : uris) {
            result.add(read(uri));
        }
        return result;
    }

    @Override
    public byte[] batcheReadMerge(List<String> uris) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        for (String uri : uris) {
            try {
                resultStream.write(read(uri));
            } catch (IOException e) {
                e.printStackTrace();
                return new byte[0];
            }
        }
        return resultStream.toByteArray();
    }

    @Override
    public long size(String path) {
        Path file = new Path(path);
        try {
            if(path.endsWith("/"))//folder
                return fileSystem.getContentSummary(file).getSpaceConsumed();
            else
            {
                FileStatus fileStatus = fileSystem.getFileStatus(file);
                return fileStatus.getLen();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String[] parts(String uri) {
        String[] result = null;
        try {
            FileStatus[] subPaths = fileSystem.listStatus(new Path(basePath.toUri().toString() + uri));
            result = new String[subPaths.length];
            for (int index = 0; index < subPaths.length; index++) {
                //System.out.println("path :" + subPaths[index].getPath().toString());
                result[index] = basePath.toUri().toString() + uri+"/"+index;//subPaths[index].getPath().toString();
                //System.out.println("path :" + result[index]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean exists(String path) {
        Path file = new Path(path);
        try {
            return fileSystem.exists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long download(String hdfs_source, String localfile_destination) {
        hdfs_source = "/"+hdfs_source;
        long readBytes = 0;
        try {
            File destFile = new File(localfile_destination);
            if (destFile.exists()) {
                destFile.delete();
            } else {
                File parent = destFile.getParentFile();
                parent.mkdirs();
                destFile.createNewFile();
            }
            FileOutputStream file = new FileOutputStream(localfile_destination);
            String[] pieces = parts(hdfs_source);
            log.info("File splited into " + pieces.length + "pieces ");
            System.out.println("File is splited into " + pieces.length + " pieces ");
            long filesize=0;
            for (String piece : pieces) {
                byte[] pieceBytes = read(piece);
                file.write(pieceBytes);
                readBytes += pieceBytes.length;
                log.info("Succesfully download " + readBytes + " bytes");
                System.out.println("Succesfully download " + pieceBytes.length + " bytes");
            }
            file.close();
            System.out.println("Download completed Total downloaded " + readBytes + " bytes into file " + localfile_destination );
//            FileInputStream fileInputStream=new FileInputStream(localfile_destination);
//            MD5Hash key = MD5Hash.digest(fileInputStream);
//            fileInputStream.close();
//            System.out.println("MD5 key : " + key);
//            log.info("MD5 key : " + key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBytes;
    }

    @Override
    public boolean initializeWriter(Properties configuration) {
        if (hdfsConfiguration == null)
            return false;
        if (fileSystem == null)
            return false;
        return true;
    }

    @Override
    public boolean write(String uri, InputStream stream) {
        SequenceFile.Writer writer = getWriterFor(uri);

        try {
            int size = stream.available();
            byte[] bytes = new byte[size];
            int readBytes = stream.read(bytes);
            if (readBytes != size) {
                log.error("Could not read all the bytes from the inputStream. Read " + readBytes + " instead of " +
                        size);
                return false;
            }
            HDFSByteChunk byteChunk = new HDFSByteChunk(bytes, uri);
            writer.append(new IntWritable(0), byteChunk);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SequenceFile.Writer getWriterFor(final String uri) {

        try {
            return ugi.doAs(new PrivilegedExceptionAction<SequenceFile.Writer>() {
                @Override
                public SequenceFile.Writer run() throws IOException {

                    SequenceFile.Writer.Option keyClass = SequenceFile.Writer.keyClass(IntWritable.class);
                    SequenceFile.Writer.Option valueClass = SequenceFile.Writer.valueClass(HDFSByteChunk.class);
                    SequenceFile.Writer.Option fileName = SequenceFile.Writer.file(new Path(basePath.toUri().toString() + "/" + uri));
                    SequenceFile.Writer writer = null;

                    writer = SequenceFile.createWriter(hdfsConfiguration, keyClass, valueClass, fileName);

                    return writer;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean write(Map<String, InputStream> stream) {
        boolean result = true;
        for (Map.Entry<String, InputStream> entry : stream.entrySet()) {
            result = result && (write(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Override
    public boolean write(String uri, List<InputStream> streams) {
        boolean result = false;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (InputStream stream : streams) {

                int size = stream.available();
                byte[] bytes = new byte[size];
                int readBytes = stream.read(bytes);
                if (readBytes != size) {
                    log.error("Could not read all the bytes from the inputStream. Read " + readBytes + " instead of " +
                            size);
                    return false;
                }
                outputStream.write(bytes);
            }
            HDFSByteChunk byteChunk = new HDFSByteChunk(outputStream.toByteArray(), uri);
            SequenceFile.Writer writer = getWriterFor(uri);
            writer.append(new IntWritable(0), byteChunk);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean writeData(String uri, byte[] data) {
    /*
    * Delete the parent folder if the parent folder exists
    * */

        File f = new File(uri);
        if (f.getName().equals(storageConfiguration.getProperty("postfix"))) {
            f = f.getParentFile();
            Path file = new Path(String.valueOf(f));
            try {
                if (fileSystem.exists(file)) {
                    fileSystem.delete(file, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SequenceFile.Writer writer = getWriterFor(uri);
        HDFSByteChunk byteChunk = new HDFSByteChunk(data, uri);
        try {
            writer.append(new IntWritable(0), byteChunk);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean writeData(Map<String, byte[]> data) {
        boolean result = true;
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            result &= writeData(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public boolean writeData(String uri, List<byte[]> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] datum : data) {
            try {
                outputStream.write(datum);
                writeData(uri, outputStream.toByteArray());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}