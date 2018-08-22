/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.filedemo.extraction;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.opencv.core.Core;
import org.springframework.core.io.Resource;

/**
 *
 * @author appcino
 */
public class ExtractPdfSignature {

    public static String extractSignature(Resource resource) throws Exception {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        String filepath = "/home/appcino/Downloads/pdfimages/test/SAMPLE PDF.pdf";
//        File sourceFile = new File(filepath);
        String filename = resource.getFilename();
        int index = filename.indexOf(".pdf");
        filename = filename.substring(0, index);
        filename = filename.replaceAll("%20"," ");
        
        System.out.println("remove all space bet filename : "+filename);
        //InputStream ins = new FileInputStream(sourceFile);
        List<String> traindata = new ArrayList<>();
        boolean flagpdf2img = convertpdftoimage(resource.getFile());
        if (flagpdf2img) {
            File file = new File("/home/appcino/Downloads/pdfimages/train/");
            File[] files = file.listFiles();

            for (File f : files) {
                traindata.add(f.getAbsolutePath());
            }
            String path = resource.getFile().getAbsolutePath();
            int lastindex=path.lastIndexOf("/");
            path=path.substring(0, lastindex+1);
            String infileocv = path+"" + filename + ".jpeg";
            String outfileocv = path+"" + filename + "-crop.jpeg";
            ObjectMatching objmatch = new ObjectMatching();
            boolean flagmatch = objmatch.matchOperation(infileocv, traindata, outfileocv);
            System.out.println("Template Matching :::::"+flagmatch);
            
            if (flagmatch) {
                String destDir = "/home/appcino/Downloads/pdfimages/result/";
                //convertImgToPDF(outfile, filename, destDir);
                convertimg2pdf(outfileocv, filename, path);
            }
        }
        return filename;
    }



    public static boolean convertpdftoimage(File sourceFile) {
        boolean flag = false;
        try {
            //String sourceDir = "/home/appcino/Downloads/pdfimages/test/" + filename + ".pdf"; // Pdf files are read from this folder
            //String destinationDir = "/home/appcino/Downloads/pdfimages/temp/"; // converted images from pdf document are saved here
            String path = sourceFile.getAbsolutePath();
            int lastindex=path.lastIndexOf("/");
            path=path.substring(0, lastindex+1);
            //File sourceFile = new File(infile);
            File destinationFile = new File(path);
            if (!destinationFile.exists()) {
                destinationFile.mkdir();
                System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
            } else {
                //FileUtils.cleanDirectory(destinationFile);
            }
            if (sourceFile.exists()) {
                System.out.println("Images copied to Folder: " + destinationFile.getName());
                PDDocument document = PDDocument.load(sourceFile);
                List<PDPage> list = document.getDocumentCatalog().getAllPages();
                System.out.println("Total files to be converted -> " + list.size());

                String fileName = sourceFile.getName().replace(".pdf", "");
                int pageNumber = 1;
                for (PDPage page : list) {
                    BufferedImage image = page.convertToImage();
                    //File outputfile = new File(destinationDir + fileName + "_" + pageNumber + ".jpeg");
                    File outputfile = new File(path + fileName + ".jpeg");
                    System.out.println("Image Created -> " + outputfile.getName());
                    ImageIO.write(image, "jpeg", outputfile);
                    pageNumber++;
                }
                document.close();
                flag = true;
                System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());
            } else {
                System.err.println(sourceFile.getName() + " File not exists");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    
    public static void convertimg2pdf(String imagePath, String fileName, String destDir) throws Exception {
        InputStream in = new FileInputStream(imagePath);
        BufferedImage bimg = ImageIO.read(in);
        float width = bimg.getWidth();
        float height = bimg.getHeight();
        Rectangle imageSize = new Rectangle(width, height);
        Document document = new Document(imageSize, 0, 0, 0, 0);
        PdfWriter.getInstance(document, new FileOutputStream(destDir + "/" + fileName + ".pdf"));
        document.open();
        Image image = Image.getInstance(imagePath);
        //document.add(new Paragraph("Your Heading for the Image Goes Here"));
        document.add(image);
        document.close();
    }
}
