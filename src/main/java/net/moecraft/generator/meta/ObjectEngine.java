//--------------------------------------------------
// Class ObjectEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import net.moecraft.generator.Environment;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ObjectEngine {
    private final static GeneratorConfig config = GeneratorConfig.getInstance();
    private int objectSize;
    private MetaResult result;

    public static String getOutDir() {
        return Environment.getBaseMoeCraftPath() + "/../Deployment";
    }

    public ObjectEngine(MetaResult result) throws IOException {
        this.result = result;
        objectSize = (int) config.getObjectSize();

        File outdir = new File(getOutDir());
        if(!outdir.exists() && !outdir.mkdirs())
            throw new IOException("Unable to create directory: " + outdir.getPath());
    }

    public void startMakeObjects() {
        result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(fileNode -> fileNode.setObjects(makeObject(fileNode)));
        result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(fileNode -> fileNode.setObjects(makeObject(fileNode)));
        scanDir(result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
    }

    private void scanDir(List<DirectoryNode> result) {
        for (DirectoryNode directoryNode : result) {
            directoryNode.getFileNodes().forEach(fileNode -> fileNode.setObjects(makeObject(fileNode)));
            if(directoryNode.hasChildDirectory())
                scanDir(directoryNode.getDirectoryNodes());
        }
    }

    public List<FileNode> makeObject(FileNode fileNode) {
        List<FileNode> result = null;

        try {
            File inputFile = fileNode.getFile();
            FileInputStream input = new FileInputStream(inputFile);

            int objectID = 0;
            boolean exitFlag = false;

            if(!this.result.hasGlobalObject(fileNode.getMD5())) {
                result = new ArrayList<>();

                while (!exitFlag) {
                    File objectFile = new File(getObjectFilePath(objectID, fileNode));
                    FileNode objectFileNode = new FileNode(objectFile, true);
                    result.add(objectFileNode);

                    FileOutputStream output = new FileOutputStream(objectFile);
                    int offset = 0;

                    for (; offset < objectSize;) {
                        byte[] buffer = new byte[objectSize];
                        byte[] zlibBuffer = new byte[objectSize];
                        int readedLength = input.read(buffer);
                        if(readedLength == -1) {
                            exitFlag = true;
                            break;
                        }

                        output.write(buffer, 0, readedLength);
                        //offset += count;
                        offset += readedLength;
                    }

                    output.close();
                    objectID++;
                }

                this.result.putGlobalObjectsByMd5(fileNode.getMD5(), result);
            } else {
                result = this.result.getGlobalObjectsByMd5(fileNode.getMD5());
            }

            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result == null ? new ArrayList<>() : result;
    }

    public static String getObjectFilePath(int objectID, FileNode source) {
        return String.format("%s/%s-%d.txt", getOutDir(), source.getMD5(), objectID);
    }

    public static void mergeObject(FileNode file) throws IOException {
        RandomAccessFile outObject = new RandomAccessFile(Environment.getCachePath().resolve(file.getFile().getName()).toFile(), "w");
        FileChannel outChannel = outObject.getChannel();

        for (FileNode object: file.getObjects()) {
            FileInputStream inObject = new FileInputStream(object.getFile());
            FileChannel inChannel =  inObject.getChannel();

        }
    }
}
