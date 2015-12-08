/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package w13memorymapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import javafx.scene.paint.Color;

/**
 *
 * @author jsf3
 */
public class W13MemoryMapping implements Observer {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        new W13MemoryMapping();
    }

    private final KochFractal koch;
    private final File fileMapped;
    private final int level;
    private TimeStamp timeStamp;

    public W13MemoryMapping() throws IOException {
        this.koch = new KochFractal();
        this.fileMapped = new File("/media/Fractal/fileMapped.tmp");

        FileOutputStream writer;
        writer = new FileOutputStream(fileMapped);

        koch.addObserver(this);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welk level gegenereerd worden?: ");
        level = scanner.nextInt();
        koch.setLevel(level);
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();

        timeStamp = new TimeStamp();
        this.writeFileMapped();
        this.readFileMapped();
        System.out.println(timeStamp.toString());
    }

    List<Edge> edges = new ArrayList<>();

    @Override
    public void update(Observable o, Object arg) {
        Edge e = (Edge) arg;
        edges.add(e);
    }

    public void writeFileMapped() throws IOException {
        timeStamp.setBegin("begin writeFileMapped");
        fileMapped.delete();
        FileChannel fc = new RandomAccessFile(fileMapped, "rw").getChannel();
        long bufferSize = 8 * 1000000;
        MappedByteBuffer mappedBB = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        long counter = 0;

        for (Edge edge : edges) {
            mappedBB.putDouble(edge.X1);
            mappedBB.putDouble(edge.Y1);
            mappedBB.putDouble(edge.X2);
            mappedBB.putDouble(edge.Y2);
            mappedBB.putDouble(Color.valueOf(edge.color).getRed());
            mappedBB.putDouble(Color.valueOf(edge.color).getGreen());
            mappedBB.putDouble(Color.valueOf(edge.color).getBlue());
            mappedBB.putInt(edge.level);
            counter++;
        }
        timeStamp.setEnd("eind writeFileMapped");
        System.out.println("Total edges written: " + counter);
    }

    public void readFileMapped() throws IOException {
        edges.clear();
        timeStamp.setBegin("begin readFileMapped");
        try (RandomAccessFile aFile = new RandomAccessFile(fileMapped.getAbsolutePath(), "r"); FileChannel inChannel = aFile.getChannel()) {
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            buffer.load();
            for (int i = 0; i < (int) (3 * Math.pow(4, level - 1)); i++) {
                double X1 = buffer.getDouble();
                double Y1 = buffer.getDouble();
                double X2 = buffer.getDouble();
                double Y2 = buffer.getDouble();
                String color = new Color(buffer.getDouble(), buffer.getDouble(), buffer.getDouble(), 1).toString();
                int level = buffer.getInt();
                
                //create edge
                Edge edge = new Edge(X1, Y1, X2, Y2, color, level);
                edges.add(edge);
            }
            timeStamp.setEnd("eind readFileMapped");
            //print read edges
            System.out.println("" + edges.size());
            for (Edge edge : edges) {
                System.out.println("-------");
                System.out.println(edge.X1);
                System.out.println(edge.Y1);
                System.out.println(edge.X2);
                System.out.println(edge.Y2);
                System.out.println(edge.color);
                System.out.println(edge.level);
            }
            buffer.clear(); // do something with the data and clear/compact it.
        }
    }
}
