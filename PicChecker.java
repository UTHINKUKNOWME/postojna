/**
 * Created by anton on 29/06/2017.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import javax.swing.*;

public class PicChecker {


    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private static void createPrintConditions(File f, String pix) {

        File[] listOfFiles = f.listFiles();
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter printWriter = null;


        boolean allreadyConditionFile = false;

        for (File file : listOfFiles) {
            if (file.isDirectory()) {

                File[] pictures = file.listFiles();


                try {
//                    System.out.println(file.getCanonicalPath() + "\\" + "condition.txt");
                    File condition = new File(file.getCanonicalPath() + "\\" + "condition.txt");
                    File end = new File(file.getCanonicalPath() + "\\" + "end.txt");
                    end.createNewFile();
                    fw = new FileWriter(condition);
                    bw = new BufferedWriter(fw);
                    printWriter = new PrintWriter(bw);

                    printWriter.println("[OutDevice]");
                    printWriter.println("DeviceName=PICsRGB");
                    printWriter.println("[ImageList]");
                    int picCounter = 0;
                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            picCounter++;
                        }
                    }

                    printWriter.println("ImageCnt=" + picCounter);

//                        File[] pictures = file1.listFiles();
                    int counter = 1;
                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            printWriter.println(counter + "=" + picture.getName());
                            counter++;
                        }
                    }

                    for (File picture : pictures) {
                        if (picture.getName().endsWith("jpg")) {
                            printWriter.println("[" + picture.getName() + "]");
                            printWriter.println("SizeName=" + pix);
                            printWriter.println("PrintCnt=1");
                            printWriter.println("BackPrint=FILE");
                            printWriter.println("BackPrintLine1=");
                            printWriter.println("BackPrintLine2=");
                            printWriter.println("Resize=FILLIN");
                            printWriter.println("DSC_Chk=FALSE");
                        }
                    }

                } catch (Exception e) {
                } finally {
                    try {
                        if (printWriter != null)
                            printWriter.close();
                    } catch (Exception e) {
                        //exception handling left as an exercise for the reader
                    }
                    try {
                        if (bw != null)
                            bw.close();
                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                    try {
                        if (fw != null)
                            fw.close();
                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                }

            }

        }
    }

    private static void checkForNewPic(File srcFile, String destFile, int max, String pix) {


        int MAX_FILES = max;
        File[] listOfFiles = srcFile.listFiles();
        boolean allreadyLogFile = false;
        int howMany = 0;
        PrintWriter printWriter = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean makeConditions = false;
        String timeStamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        String folderTimeStamp = new SimpleDateFormat("MM.dd.yyyy-HH-mm-ss").format(Calendar.getInstance().getTime());

        try {
            File progFile = new File(destFile);
            File[] listOfFiles1 = progFile.listFiles();
            for (int i = 0; i < listOfFiles1.length; i++) {
                if (listOfFiles1[i].isFile()) {
//                    System.out.println("File " + listOfFiles[i].getName());
                    if (listOfFiles1[i].getName().equals("log.txt")) {
                        allreadyLogFile = true;
                    }
                }
            }

            File log = new File(destFile + "/" + "log.txt");
            if (allreadyLogFile) {

                fw = new FileWriter(log, true);
                bw = new BufferedWriter(fw);
                printWriter = new PrintWriter(bw);

            } else {
//                System.out.println(allreadyLogFile);
                fw = new FileWriter(log);
                bw = new BufferedWriter(fw);
                printWriter = new PrintWriter(bw);
            }

//            String newPics = "newPics";
            int counter = 1;
            printWriter.println("-----------------------------------"); //35 crti
            printWriter.print("Program started at " + timeStamp);
            printWriter.println();
            printWriter.println();
            boolean flag = true;

            int k = 0;
            int start = 0;
            for (int i = start; i < listOfFiles.length; i++) {
                String dat = destFile + "/" + folderTimeStamp + "folderNum" + counter;
//                System.out.println(dat);
                File dir = new File(dat);

                while (k < MAX_FILES) {
                    int c = 0;
                    if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jpg")) {

                        if (listOfFiles[i].getName().endsWith(".jpg") && !listOfFiles[i].getName().endsWith("DONE.jpg")) {
                            makeConditions = true;
                            while (flag) {
                                if (!dir.exists()) {
                                    dir.mkdir();

                                    printWriter.print("New folder created : " + folderTimeStamp + "folderNum" + counter);
                                    printWriter.println();
                                    flag = false;
                                } else if (dir.listFiles().length < MAX_FILES) {
                                    int razlika = MAX_FILES - dir.listFiles().length;

                                    flag = false;
                                    while (c < razlika) {
//                                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jpg")) {
                                        if (!listOfFiles[i].getName().endsWith("DONE.jpg")) {
//                                            System.out.println("File being moved : " + listOfFiles[i].getName());
                                            printWriter.print("Picture being moved : " + listOfFiles[i].getName());
                                            printWriter.println();
//                                            Write done on every source pic
                                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                            copyFileUsingStream(listOfFiles[i], newFile);
//                                            ================================================

                                            String path = dir + "\\" + listOfFiles[i].getName();

                                            listOfFiles[i].renameTo(new File(path));
                                            c++;
                                            i++;
                                        } else {

                                            i++;
                                        }
//                                        }
                                    }
//                                i = start;
//                                flag1 = false;
                                } else {
                                    counter++;
                                    dat = destFile + "/" + folderTimeStamp + "folderNum" + counter;
//                                    System.out.println("--=-=-=-=-=-==-");
//                                    System.out.println(dat);
                                    dir = new File(dat);
//                                    System.out.println(dat);
                                }

                            }
                            if (c > 0) {
                                break;
                            }


                            printWriter.println("File being moved : " + listOfFiles[i].getName());
//                            Write done on every pic
                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                            copyFileUsingStream(listOfFiles[i], newFile);
//                          ================================================
//                            newFile.renameTo(new File(dir + "/" + newFile.getName()));
                            String path1 = dir + "\\" + listOfFiles[i].getName();
                            System.out.println("The path is : " + path1);
                            listOfFiles[i].renameTo(new File(path1));
                            k++;
                            i++;
                        } else {
                            i++;
                        }

                    } else {
//                        if (i < listOfFiles.length) {
                        i++;
//                        }
                    }

                }

                i = start;
                flag = true;
                k = 0;
                counter++;

            }


        } catch (Exception e) {

//            System.out.println(e);
        } finally {
//            System.out.println(makeConditions);
            if (makeConditions) {
//                System.out.println("We are in!");
                createPrintConditions(new File(destFile), pix);
            }

            try {
                if (printWriter != null)
                    printWriter.close();
            } catch (Exception e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
//        System.out.println("Pictures renamed and moved: " + howManyPrint);

    }

    public static class checkForNewPics extends TimerTask {

        File srcFolder;
        int maxFiles;
        String dest;
        String pix;


        checkForNewPics(File f, String dest, int maxFiles, String pix) {
            srcFolder = f;
            this.maxFiles = maxFiles;
            this.dest = dest;
            this.pix = pix;
        }

        public void run() {
//            File folder = new File("../sliki");
            checkForNewPic(srcFolder, dest, maxFiles, pix);
        }
    }


    public static void main(String[] args) throws Exception {

        final String[] s = new String[2];

        JFrame frame = new JFrame("Picture Checker");

        frame.setLocation(200, 200);
//        frame.setLayout(new GridLayout(2,1));
        Container cont = frame.getContentPane();

//        Container cont1 = frame.getContentPane();

//        cont.setLayout(new GridLayout(3, 2));
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
//        cont1.setLayout(new GridLayout(1,2));
//        JLabel chooseFolderLabel1 = new JLabel("Choose the destination folder");
//        chooseFolderLabel1.setHorizontalAlignment(SwingConstants.CENTER);
//        cont.add(chooseFolderLabel1);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel setMaxFilesText = new JLabel("Max files per folder");
        setMaxFilesText.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(setMaxFilesText);

        JTextField maxFiles = new JTextField();
        maxFiles.setText("6");
        maxFiles.setColumns(3);
        panel.add(maxFiles);

        JLabel setInterval = new JLabel();
        setInterval.setText("Choose the interval ");
        setInterval.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(setInterval);


        JTextField interval = new JTextField();
        interval.setText("5");
        interval.setColumns(3);
        panel.add(interval);

        cont.add(panel);

        JPanel pane = new JPanel();

        JLabel chooseFolderLabel = new JLabel("Choose the destination then source folder");
        chooseFolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pane.add(chooseFolderLabel);


        JButton btnBrowse = new JButton("Browse");
        pane.add(btnBrowse);
        cont.add(pane);

        btnBrowse.addActionListener(new ActionListener() {
            String z;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                JFileChooser fileChooser1 = new JFileChooser();
                fileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                String[] possibilities = {"8x10", "10x12", "10x15", "11x14", "12x16", "13x18"};
                String s = (String) JOptionPane.showInputDialog(
                        frame,
                        "Choose the size ",
                        "Size Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities,
                        "13x18");

                int rVal1 = fileChooser1.showOpenDialog(null);

                if (rVal1 == fileChooser1.APPROVE_OPTION) {
                    z = fileChooser1.getSelectedFile().toString();
//                    System.out.println(fileChooser.getSelectedFile().toString());
                }

//                System.out.println(z);
                int rVal = fileChooser.showOpenDialog(null);

                if (rVal == JFileChooser.APPROVE_OPTION) {
//                    System.out.println(fileChooser.getSelectedFile().toString());
                    Timer timer = new Timer();
                    if (!maxFiles.getText().equals(null) && !interval.getText().equals(null)) {

                        int interval1 = Integer.parseInt(interval.getText()) * 1000;
                        int maxF = Integer.parseInt(maxFiles.getText());
//                        System.out.println(interval1);
//                        System.out.println(z);
                        timer.schedule(new checkForNewPics(new File(fileChooser.getSelectedFile().toString()), z, maxF, s), 0, interval1);
                    }

                }

            }


        });

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }

}

