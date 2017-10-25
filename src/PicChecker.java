/**
 * Created by anton on 29/06/2017.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import javax.swing.*;

public class PicChecker {


    final static String mainpath = "C:\\Users\\Antonio\\Desktop\\";

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
                    System.out.println(e);
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

        String logFolderPath = mainpath + "logs";
        int MAX_FILES = max;
        File[] listOfFiles = srcFile.listFiles();
        boolean allreadyLogFile = false;
        PrintWriter printWriter = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean makeConditions = false;
        int howManyUntilBreak = 0;
        String timeStamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        String logTimeStap = new SimpleDateFormat("MMddyyyy").format(Calendar.getInstance().getTime());
//        String folderTimeStamp = new SimpleDateFormat("MM.dd.yyyy-HH-mm-ss").format(Calendar.getInstance().getTime());
        String folderTimeStamp = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

        try {
            File progFile = new File(logFolderPath);
            File[] listOfFiles1 = progFile.listFiles();
            for (int i = 0; i < listOfFiles1.length; i++) {
                if (listOfFiles1[i].isFile()) {
//                    System.out.println("File " + listOfFiles[i].getName());
                    if (listOfFiles1[i].getName().equals(logTimeStap + "-log.txt")) {
                        allreadyLogFile = true;
                    }
                }
            }

            File log = new File(logFolderPath + "/" + logTimeStap + "-log.txt");
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
            if (checkIfThereAreMaxNew(listOfFiles, max)) {
                for (int i = start; i < listOfFiles.length; i++) {
//                boolean firstTime = true;
                String dat = destFile + "/" + folderTimeStamp;
//                    String dat = destFile + "/" + folderTimeStamp + "-" + counter;
//                System.out.println(dat);
                    File dir = new File(dat);

                    while (k < MAX_FILES) {
                        int c = 0;
                        if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".jpg") || listOfFiles[i].getName().endsWith(".JPG"))) {

                            if ((listOfFiles[i].getName().endsWith(".jpg") && !listOfFiles[i].getName().endsWith("DONE.jpg")) || (listOfFiles[i].getName().endsWith(".JPG") && !listOfFiles[i].getName().endsWith("DONE.JPG"))) {
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
                                            if (!listOfFiles[i].getName().endsWith("DONE.jpg") || !listOfFiles[i].getName().endsWith("DONE.JPG")) {
//                                            System.out.println("File being moved : " + listOfFiles[i].getName());
                                                printWriter.print("Picture being moved : " + listOfFiles[i].getName());
                                                printWriter.println();
                                                howManyUntilBreak++;
//                                            Write done on every source pic
                                                File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                                if (listOfFiles[i].getName().endsWith(".jpg")) {
                                                    newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                                }

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
//                                        dat = destFile + "/" + folderTimeStamp + "-" + counter;
                                    dat = destFile + "/" + folderTimeStamp;
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
                                howManyUntilBreak++;
                                File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                if (listOfFiles[i].getName().endsWith(".jpg")) {
                                    newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                }
                                copyFileUsingStream(listOfFiles[i], newFile);
//                          ================================================
//                            newFile.renameTo(new File(dir + "/" + newFile.getName()));
                                String path1 = dir + "\\" + listOfFiles[i].getName();
                                System.out.println("The path is : " + path1);

                                listOfFiles[i].renameTo(new File(path1));

//                            if(firstTime) {
//                                try {
//                                    System.out.println(dir);
//                                    Runtime r = Runtime.getRuntime();
//                                    r.exec("C:\\Users\\Antonio\\Desktop\\OBDELAJ.exe" + " " + dir);
//                                    firstTime = false;
//                                } catch (IOException err) {
//                                    err.printStackTrace();
//
//                                }
//                            }

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
                    if (howManyUntilBreak == max) {
                        break;
                    }
                    i = start;
                    flag = true;
                    k = 0;
                    counter++;

                }

            }
        } catch (Exception e) {

            System.out.println(e);
//            printWriter.println(e);
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

    private static boolean checkIfThereAreMaxNew(File[] folder, int max) {
        boolean out = false;
        int counter = 0;
//        File[] listOfFiles = folder.listFiles();
        for (File file : folder) {

            if (file.isFile() && ((file.getName().endsWith(".jpg") && !file.getName().endsWith("DONE.jpg")) || (file.getName().endsWith(".JPG") && !file.getName().endsWith("DONE.JPG")))) {
                counter++;
            }
        }
        System.out.println("There are " + counter + " files.");
        return counter >= max;
    }

    private static void checkForNewPicFirst(File srcFile, String destFile, int max) {

        int openedInPs = 0;
        int MAX_FILES = max;
        File[] listOfFiles = srcFile.listFiles();
        boolean allreadyLogFile = false;
        int howMany = 0;
//        PrintWriter printWriter = null;
//        BufferedWriter bw = null;
//        FileWriter fw = null;
        boolean makeConditions = false;
        String timeStamp = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
//        String folderTimeStamp = new SimpleDateFormat("MM.dd.yyyy-HH-mm-ss").format(Calendar.getInstance().getTime());
        String folderTimeStamp = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

        try {
//            File progFile = new File(destFile);
//            File[] listOfFiles1 = progFile.listFiles();
//            for (int i = 0; i < listOfFiles1.length; i++) {
//                if (listOfFiles1[i].isFile()) {
////                    System.out.println("File " + listOfFiles[i].getName());
//                    if (listOfFiles1[i].getName().equals("log.txt")) {
//                        allreadyLogFile = true;
//                    }
//                }
//            }
//
//            File log = new File(destFile + "/" + "log.txt");
//            if (allreadyLogFile) {
//
//                fw = new FileWriter(log, true);
//                bw = new BufferedWriter(fw);
//                printWriter = new PrintWriter(bw);
//
//            } else {
////                System.out.println(allreadyLogFile);
//                fw = new FileWriter(log);
//                bw = new BufferedWriter(fw);
//                printWriter = new PrintWriter(bw);
//            }

//            String newPics = "newPics";
//            int counter = 1;
//            printWriter.println("-----------------------------------"); //35 crti
//            printWriter.print("Program started at " + timeStamp);
//            printWriter.println();
//            printWriter.println();
            boolean flag = true;

            int k = 0;
            int start = 0;
            for (int i = start; i < listOfFiles.length; i++) {
                boolean firstTime = true;
                String dat = destFile + "/" + folderTimeStamp;
//                String dat = destFile + "/" + folderTimeStamp + "-" + counter;
//                System.out.println(dat);
                File dir = new File(dat);

                while (k < MAX_FILES) {
                    int c = 0;
                    if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".jpg") || listOfFiles[i].getName().endsWith(".JPG"))) {

                        if ((listOfFiles[i].getName().endsWith(".jpg") && !listOfFiles[i].getName().endsWith("DONE.jpg")) || (listOfFiles[i].getName().endsWith(".JPG") && !listOfFiles[i].getName().endsWith("DONE.JPG"))) {
//                            makeConditions = true;
                            while (flag) {
                                if (!dir.exists()) {
                                    dir.mkdir();

//                                    printWriter.print("New folder created : " + folderTimeStamp + "folderNum" + counter);
//                                    printWriter.println();
                                    flag = false;
                                } else if (dir.listFiles().length < MAX_FILES) {
                                    int razlika = MAX_FILES - dir.listFiles().length;

                                    flag = false;
                                    while (c < razlika) {
//                                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jpg")) {
                                        if (!listOfFiles[i].getName().endsWith("DONE.jpg") || !listOfFiles[i].getName().endsWith("DONE.JPG")) {
//                                            System.out.println("File being moved : " + listOfFiles[i].getName());
//                                            printWriter.print("Picture being moved : " + listOfFiles[i].getName());
//                                            printWriter.println();
//                                            Write done on every source pic


                                            try {
                                                System.out.println(listOfFiles[i].getCanonicalPath());
                                                Runtime r = Runtime.getRuntime();
                                                r.exec(mainpath + "OBDELAJ.exe" + " " + listOfFiles[i].getCanonicalPath());
                                                openedInPs++;
                                            } catch (IOException err) {
                                                err.printStackTrace();
                                            }

                                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                                            if (listOfFiles[i].getName().endsWith(".jpg")) {
                                                newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                                            }
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
//                                    counter++;
//                                    dat = destFile + "/" + folderTimeStamp + "-" + counter;
                                    dat = destFile + "/" + folderTimeStamp;
//                                    System.out.println("--=-=-=-=-=-==-");
//                                    System.out.println(dat);
                                    dir = new File(dat);
//                                    System.out.println(dat);
                                }

                            }
                            if (c > 0) {
                                break;
                            }


//                            printWriter.println("File being moved : " + listOfFiles[i].getName());
                            File newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".JPG", "DONE.JPG"));
//                            Write done on every pic
                            if (listOfFiles[i].getName().endsWith(".jpg")) {
                                newFile = new File(srcFile + "\\" + listOfFiles[i].getName().replace(".jpg", "DONE.jpg"));
                            }
                            copyFileUsingStream(listOfFiles[i], newFile);
//                          ================================================
//                            newFile.renameTo(new File(dir + "/" + newFile.getName()));
                            String path1 = dir + "\\" + listOfFiles[i].getName();
                            System.out.println("The path is : " + path1);

                            listOfFiles[i].renameTo(new File(path1));

                            if (firstTime) {
                                try {
                                    System.out.println(dir);
                                    Runtime r = Runtime.getRuntime();
                                    r.exec(mainpath + "OBDELAJ.exe" + " " + dir);
                                    firstTime = false;
                                    openedInPs++;
                                } catch (IOException err) {
                                    err.printStackTrace();

                                }
                            }

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
                System.out.println(openedInPs);
                if (openedInPs == 1) {
                    break;
                }
                i = start;
                flag = true;
                k = 0;
//                counter++;

            }


        } catch (Exception e) {
//       Mi printe samo arrayoutofbounds exception izmegju interval
//            System.out.println(e);
        }

    }


    public static void emptyFolderFull(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                for (File file1 : file.listFiles()) {
                    file1.delete();
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    public static void emptyFolder(File folder) {

        if (folder.listFiles().length > 10) {

            System.out.println("Deleting temp folders.");
            File[] files = folder.listFiles();
            for (int i = 0; i < 10; i++) {
                System.out.println(files[i].getName());
                if (files[i].isDirectory()) {
                    for (File file1 : files[i].listFiles()) {
                        file1.delete();
                    }
                    files[i].delete();
                } else {
                    files[i].delete();
                }
            }
        }
    }

    public static class checkForNewPicsTimer extends TimerTask {

        File srcFolder;
        int maxFiles;
        String dest;
        String pix;
        String temp = mainpath + "temp";

        checkForNewPicsTimer(File f, String dest, int maxFiles, String pix) {
            srcFolder = f;
            this.maxFiles = maxFiles;
            this.dest = dest;
            this.pix = pix;
        }

        public void run() {

            checkForNewPic(srcFolder, dest, maxFiles, pix);
        }
    }

    public static class checkForNewPicsFirstTimer extends TimerTask {

        File srcFolder;
        int maxFiles;
        String dest;
        String pix;
        String output = mainpath + "OUTPUT FOLDER DROP";

        checkForNewPicsFirstTimer(File f, String dest, int maxFiles, String pix) {
            srcFolder = f;
            this.maxFiles = maxFiles;
            this.dest = dest;
            this.pix = pix;
        }

        public void run() {
//            File folder = new File("../sliki");
            checkForNewPicFirst(srcFolder, dest, maxFiles);

        }
    }

    public static class emptyTempFolderTimer extends TimerTask {

        String temp = mainpath + "temp";


        public void run() {
            emptyFolder(new File(temp));
        }
    }



    public static void main(String[] args) throws Exception {

        String temp = mainpath + "temp";
        String output = mainpath + "OUTPUT FOLDER DROP";


        final String[] s = new String[2];

        String fontName = "Arial";
        int fontSize = 15;

        Integer[] seconds = {15, 20, 30, 50, 60};
        Integer[] filesInFolder = {10, 30, 50};
        String[] possibilities = {"13x18", "8x10", "10x12", "10x15", "11x14", "12x16"};
//        String[] possibilities = {"13x18"};

        JFrame frame = new JFrame("Postojna Cave");

        ImageIcon ImageIcon = new ImageIcon("icona.png");
        Image image = ImageIcon.getImage();

        frame.setIconImage(image);

        frame.setLocation(400, 400);
//        frame.setPreferredSize(new Dimension(400,200));

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
//        setMaxFilesText.setFont(new Font(fontName,Font.BOLD,fontSize));
        panel.add(setMaxFilesText);

        JComboBox<Integer> filesComboBox = new JComboBox<>(filesInFolder);
        panel.add(filesComboBox);

//        JTextField maxFiles = new JTextField();
//        maxFiles.setText("6");
//        maxFiles.setColumns(3);
//        panel.add(maxFiles);

        JLabel setInterval = new JLabel();
        setInterval.setText("Choose the interval ");
        setInterval.setHorizontalAlignment(SwingConstants.CENTER);
//        setInterval.setFont(new Font(fontName,Font.BOLD,fontSize));
        panel.add(setInterval);

        JComboBox<Integer> intervalComboBox = new JComboBox<>(seconds);
        panel.add(intervalComboBox);
//        JTextField interval = new JTextField();
//        interval.setText("5");
//        interval.setColumns(3);
//        panel.add(interval);

        JLabel formatLabel = new JLabel();
        formatLabel.setText("Choose a format");
        formatLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(formatLabel);

        JComboBox<String> formatComboBox = new JComboBox<>(possibilities);
        panel.add(formatComboBox);

        cont.add(panel);

        JPanel sourcePanel = new JPanel();

        JLabel chooseSourceFolderLabel = new JLabel("Choose the source folder");
        chooseSourceFolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        chooseFolderLabel.setFont(new Font(fontName,Font.BOLD,fontSize));
        sourcePanel.add(chooseSourceFolderLabel);


        JButton btnBrowse = new JButton("Browse");
        sourcePanel.add(btnBrowse);
        cont.add(sourcePanel);

//        JPanel sourceLabelPanel = new JPanel();

        JLabel src = new JLabel("Source: ...");
        sourcePanel.add(src);
        cont.add(sourcePanel);

        JPanel destPanel = new JPanel();

        JLabel chooseDestFolderLabel = new JLabel("Choose the destination folder");
        chooseDestFolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        chooseFolderLabel.setFont(new Font(fontName,Font.BOLD,fontSize));
        destPanel.add(chooseDestFolderLabel);

        JButton btnBrowse1 = new JButton("Browse");
//        JButton start = new JButton("Start the program");
        destPanel.add(btnBrowse1);
//        destPanel.add(start);
        cont.add(destPanel);

        JLabel dest = new JLabel("Destination: ...");
        destPanel.add(dest);
//        destPanel.add(run);
        cont.add(destPanel);


        JPanel runPanel = new JPanel();
        JButton run = new JButton("Start the program");
        runPanel.add(run);
        cont.add(runPanel);




        btnBrowse.addActionListener(new ActionListener() {
            //            String destination;
            String source;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Choose the source folder");

                int rVal = fileChooser.showOpenDialog(null);

                if (rVal == JFileChooser.APPROVE_OPTION) {
//                    System.out.println(fileChooser.getSelectedFile().toString());

//                    Timer timer = new Timer();
                    source = fileChooser.getSelectedFile().toString();
                    src.setText(source);


                }

            }


        });

        btnBrowse1.addActionListener(new ActionListener() {
            String destination;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Choose the destination folder");

                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    destination = fileChooser.getSelectedFile().toString();
                    dest.setText(destination);
                }


            }
        });


/// TRYIIONG OUT GITHUB


        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



                String intervalBoxValue = intervalComboBox.getSelectedItem().toString();
                String filesBoxValue = filesComboBox.getSelectedItem().toString();
                String formatBoxValue = formatComboBox.getSelectedItem().toString();
//                    if (!maxFiles.getText().equals(null) && !interval.getText().equals(null)) {
                if (!filesBoxValue.equals(null) && !intervalBoxValue.equals(null)) {
//                        int interval1 = Integer.parseInt(interval.getText()) * 1000;
                    int interval1 = Integer.parseInt(intervalBoxValue) * 1000;
                    int maxF = Integer.parseInt(filesBoxValue);

                    Timer timer21 = new Timer();
                    Timer timer31 = new Timer();
//
                    timer21.schedule(new checkForNewPicsFirstTimer(new File(src.getText()), temp, 10, formatBoxValue), 0, 15000);

                    timer21.schedule(new emptyTempFolderTimer(), 0, 15000);

                    timer31.schedule(new checkForNewPicsTimer(new File(output), dest.getText(), maxF, formatBoxValue), 0, interval1);


                    JOptionPane.showMessageDialog(null, "The program is running on every " + interval1 / 1000 + " seconds");


                }
            }
        });


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                File temp = new File(mainpath + "temp");
                if (temp.listFiles().length > 0) {
                    emptyFolderFull(temp);
                }
            }
        });

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }

}

