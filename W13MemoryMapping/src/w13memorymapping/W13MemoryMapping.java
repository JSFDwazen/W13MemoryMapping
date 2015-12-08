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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        W13MemoryMapping w13MemoryMapping = new W13MemoryMapping();
    }

    private KochFractal koch;
    private File fileMapped;
    private int level;

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
        //TimeStamp timeStamp = new TimeStamp();
        this.writeFileMapped();
        this.readFileMapped();
    }

    List<Edge> edges = new ArrayList<>();

    @Override
    public void update(Observable o, Object arg) {
        Edge e = (Edge) arg;
        edges.add(e);
    }

    public void writeFileMapped() throws IOException {
        fileMapped.delete();
        FileChannel fc = new RandomAccessFile(fileMapped, "rw").getChannel();

        long bufferSize = 8 * 1000;
        MappedByteBuffer mappedBB = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);

        int start = 0;
        long counter = 0;
        long HUNDREDK = 100000;
        long startT = System.currentTimeMillis();
        long noOfMessage = HUNDREDK * 10 * 10;

        for (Edge edge : edges) {
            mappedBB.putDouble(edge.X1);
            mappedBB.putDouble(edge.Y1);
            mappedBB.putDouble(edge.X2);
            mappedBB.putDouble(edge.Y2);
            mappedBB.putDouble(Color.valueOf(edge.color).getRed());
            mappedBB.putDouble(Color.valueOf(edge.color).getGreen());
            mappedBB.putDouble(Color.valueOf(edge.color).getBlue());
            //System.out.println(Arrays.toString(edge.color.getBytes()));
            //mappedBB.put(edge.color.getBytes());
            mappedBB.putInt(edge.level);
            counter++;
        }
        System.out.println("Total edges written: " + counter);
    }

    public void readFileMapped() throws IOException {
        edges.clear();
        RandomAccessFile aFile = new RandomAccessFile(fileMapped.getAbsolutePath(), "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();
        for (int i = 0; i < (int) (3 * Math.pow(4, level - 1)); i++) {
            double X1 = buffer.getDouble();
            double Y1 = buffer.getDouble();
            double X2 = buffer.getDouble();
            double Y2 = buffer.getDouble();
            String color = new Color(buffer.getDouble(), buffer.getDouble(), buffer.getDouble(), 1).toString();
            //byte[] bytes = buffer.get
            //String color = new String(buffer.get(bytes).array(), Charset.forName("UTF-8"));
            int level = buffer.getInt();

            Edge edge = new Edge(X1, Y1, X2, Y2, color, level);
            edges.add(edge);
        }
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
        inChannel.close();
        aFile.close();
    }
}
