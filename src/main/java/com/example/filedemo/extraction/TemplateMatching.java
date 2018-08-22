/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.filedemo.extraction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author appcino
 */
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class ObjectMatching {

    public boolean matchOperation(String inFile, List<String> templates, String outFile) {
        boolean flag = false;
        for (String template : templates) {
            System.out.println("matching data  " + inFile);
            System.out.println(template);
            System.out.println(outFile);

            flag = run(inFile, template, outFile, Imgproc.TM_CCOEFF_NORMED);
            if (flag) {
                break;
            }
        }
        return flag;
    }

    public boolean run(String inFile, String templateFile, String outFile,
            int match_method) {

        boolean flag = false;
        System.out.println("\nRunning Template Matching");

        Mat img = Imgcodecs.imread(inFile);
        Mat templ = Imgcodecs.imread(templateFile);

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF
                || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(255, 255, 255));
        System.out.println(matchLoc.x);
        System.out.println(templ.cols());
        System.out.println(matchLoc.y);
        System.out.println(templ.rows());
        System.out.println("match score max" + mmr.maxVal);
        System.out.println("match score min" + mmr.minVal);
        // Save the visualized detection.
        double minMatchQuality = 0.8;
        if (mmr.maxVal > minMatchQuality) // with CV_TM_SQDIFF_NORMED use minValue < minMatchQuality
        {
            double[] cord = {matchLoc.x, matchLoc.y, templ.cols(), templ.rows()};
            Rect rectCrop = new Rect(cord);
            Mat imageROI = img.submat(rectCrop);
            System.out.println("Writing " + outFile);
            Imgcodecs.imwrite(outFile, imageROI);
            flag = true;
        } else {
            System.out.println("File Not Matched");
        }
        return flag;
    }
}

public class TemplateMatching {

    public static void main(String[] args) {

        //System.loadLibrary("opencv_java300");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String infile = "/home/appcino/Downloads/spring-boot-file-upload-download-rest-api-example-master/uploads/SIG CARD - PR.jpeg";
        String template = "/home/appcino/Downloads/pdfimages/form3_sig.jpeg";
        String outfile = "/home/appcino/Downloads/pdfimages/form3_sigtemp3_1_2.jpeg";

        //new MatchingDemo().run(args[0], args[1], args[2], Imgproc.TM_CCOEFF);
        new ObjectMatching().run(infile, template, outfile, Imgproc.TM_CCOEFF_NORMED);
    }

}
