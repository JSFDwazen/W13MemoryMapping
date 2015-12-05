/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package w13memorymapping;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    KochFractal koch = new KochFractal();
    private File fileMapped;

    public W13MemoryMapping() throws IOException {
        this.fileMapped = new File("/media/Fractal/fileMapped.tmp");

        FileOutputStream writer;
        writer = new FileOutputStream(fileMapped);

        koch.addObserver(this);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welk level gegenereerd worden?: ");
        int level = scanner.nextInt();
        koch.setLevel(level);
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
        //TimeStamp timeStamp = new TimeStamp();
        writeFileMapped();
    }

    ArrayList<Edge> lijst = new ArrayList<>();

    @Override
    public void update(Observable o, Object arg) {
        Edge e = (Edge) arg;
        lijst.add(e);
    }

    public void writeFileMapped() throws IOException {
        fileMapped.delete();
        FileChannel fc = new RandomAccessFile(fileMapped, "rw").getChannel();

        long bufferSize = 8 * 1000;
        MappedByteBuffer mappedBB = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        

        int start = 0;
        long counter = 1;
        long HUNDREDK = 100000;
        long startT = System.currentTimeMillis();        
        long noOfMessage = HUNDREDK * 10 * 10;
        
        for (Edge edge : lijst){
            mappedBB.putDouble(edge.X1);
            mappedBB.putDouble(edge.X2);
            mappedBB.putDouble(edge.Y1);
            mappedBB.putDouble(edge.Y2);
            mappedBB.put(edge.color.getBytes());
            mappedBB.putInt(edge.level);      
            counter++;
            System.out.println("Total edges written: " + counter);
        }
    }
}


