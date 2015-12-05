package w13memorymapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.io.*;
import java.util.*;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.concurrent.CountDownLatch;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author jsf3
 */
public class Kochweek12 implements Observer {

    KochFractal koch = new KochFractal();
    private File fileBtxt;
    private File fileUtxt;
    private File fileBbin;
    private File fileUbin;

    public Kochweek12() throws IOException {
        this.fileBtxt = new File("/media/Fractal/Btxt.txt");
        this.fileUtxt = new File("/media/Fractal/Utxt.txt");
        this.fileBbin = new File("/media/Fractal/Bbin.bin");
        this.fileUbin = new File("/media/Fractal/Ubin.bin");
        FileOutputStream writer;
        writer = new FileOutputStream(fileBtxt);
        writer = new FileOutputStream(fileUtxt);
        writer = new FileOutputStream(fileBbin);
        writer = new FileOutputStream(fileUbin);
        koch.addObserver(this);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welk level gegenereerd worden?: ");
        int level = scanner.nextInt();
        koch.setLevel(level);
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("begin bin");
        writeBin(lijst);
        timeStamp.setEnd("eind bin");
        timeStamp.setBegin("begin bin buf");
        writeBinBuffered(lijst);
        timeStamp.setEnd("eind bin buf");
        timeStamp.setBegin("begin text");        
        writeStringBuffered(lijst);
        timeStamp.setEnd("eind text");
        timeStamp.setBegin("begin text buf");
        writeString(lijst);
        timeStamp.setEnd("eind text buf");
        System.out.println(timeStamp.toString());
    }

    ArrayList<Edge> lijst = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Kochweek12 kw12 = new Kochweek12();
        // TODO code application logic here
    }

    @Override
    public void update(Observable o, Object arg) {
        Edge e = (Edge) arg;
        lijst.add(e);

    }

    public void writeBin(ArrayList<Edge> e) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileUbin, true);
            ObjectOutputStream objectout = null;
            try {
                objectout = new ObjectOutputStream(out);
            } catch (IOException ex) {
                Logger.getLogger(Kochweek12.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                objectout.writeInt(e.size());
                for (Edge x : e) {
                    objectout.writeObject(x);
                }
            } catch (IOException ex) {
                Logger.getLogger(Kochweek12.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Kochweek12.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Kochweek12.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public void writeBinBuffered(ArrayList<Edge> e) throws IOException {

        FileOutputStream file = new FileOutputStream(fileBbin, true);
        BufferedOutputStream bout = new BufferedOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeInt(e.size());
        for (Edge x : e) {
            out.writeObject(x);
        }
        out.close();
    }

    public void writeString(ArrayList<Edge> e) throws IOException {
        String Edgestring = "";
        for (Edge edge : e) {
            Edgestring += edge.X1 + " " + edge.Y1 + " " + edge.X2 + " " + edge.Y2 + " " + edge.color + " " + edge.level + "\n";
        }
        FileWriter Bw = new FileWriter(fileUtxt, true);
        Bw.write(Edgestring);
        Bw.close();
    }

    public void writeStringBuffered(ArrayList<Edge> e) throws IOException {
        String Edgestring = "";
        for (Edge edge : e) {
            Edgestring += edge.X1 + " " + edge.Y1 + " " + edge.X2 + " " + edge.Y2 + " " + edge.color + " " + edge.level + "\n";
        }
        BufferedWriter Bw = new BufferedWriter(new FileWriter(fileBtxt, true));
        Bw.write(Edgestring);
        Bw.close();
    }
}
