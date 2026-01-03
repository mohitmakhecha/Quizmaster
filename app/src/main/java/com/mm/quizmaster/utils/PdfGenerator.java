package com.mm.quizmaster.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.mm.quizmaster.database.DatabaseHelper;
import com.mm.quizmaster.models.Question;
import com.mm.quizmaster.models.QuizAnswer;
import com.mm.quizmaster.models.Subject;
import com.mm.quizmaster.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * PDF Generator Utility Class
 * Generates detailed quiz result PDF with all questions and answers
 */
public class PdfGenerator {

    /**
     * Generate detailed quiz result PDF
     * Shows all questions with student's answers and correct answers
     */
    public static boolean generateQuizResultPdf(Context context, int resultId, String studentName,
                                                String subjectName, int semester, String level,
                                                int score, int totalQuestions, double percentage) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            
            // Get quiz answers
            List<QuizAnswer> quizAnswers = dbHelper.getQuizAnswersByResultId(resultId);
            
            if (quizAnswers.isEmpty()) {
                Toast.makeText(context, "No quiz data found", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Create PDF document
            PdfDocument document = new PdfDocument();
            int pageWidth = 595; // A4 width in points
            int pageHeight = 842; // A4 height in points
            
            int currentPage = 1;
            int yPosition = 80;
            int questionNumber = 1;
            
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // Draw header on first page
            drawHeader(canvas, paint, studentName, subjectName, semester, level, score, totalQuestions, percentage);
            yPosition = 200;

            // Draw each question and answer
            for (QuizAnswer answer : quizAnswers) {
                Question question = dbHelper.getQuestionById(answer.getQuestionId());
                if (question == null) continue;

                // Check if we need a new page
                if (yPosition > 750) {
                    document.finishPage(page);
                    currentPage++;
                    pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    yPosition = 50;
                }

                // Draw question number and text
                paint.setColor(Color.BLACK);
                paint.setTextSize(12);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("Q" + questionNumber + ". " + question.getQuestionText(), 40, yPosition, paint);
                yPosition += 20;

                // Draw options
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                paint.setTextSize(10);
                
                String[] options = {
                    "A) " + question.getOptionA(),
                    "B) " + question.getOptionB(),
                    "C) " + question.getOptionC(),
                    "D) " + question.getOptionD()
                };
                
                for (String option : options) {
                    canvas.drawText(option, 60, yPosition, paint);
                    yPosition += 15;
                }
                yPosition += 5;

                // Draw student's answer
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                if (answer.isCorrect()) {
                    paint.setColor(Color.rgb(0, 128, 0)); // Green for correct
                    canvas.drawText("Your Answer: " + answer.getSelectedAnswer() + " ✓ Correct", 60, yPosition, paint);
                } else {
                    paint.setColor(Color.rgb(255, 0, 0)); // Red for wrong
                    canvas.drawText("Your Answer: " + answer.getSelectedAnswer() + " ✗ Wrong", 60, yPosition, paint);
                    yPosition += 15;
                    paint.setColor(Color.rgb(0, 128, 0));
                    canvas.drawText("Correct Answer: " + question.getCorrectAnswer(), 60, yPosition, paint);
                }
                
                yPosition += 25;
                
                // Draw separator line
                paint.setColor(Color.LTGRAY);
                paint.setStrokeWidth(1);
                canvas.drawLine(40, yPosition, 555, yPosition, paint);
                yPosition += 15;
                
                questionNumber++;
            }

            // Finish last page
            document.finishPage(page);

            // Save PDF using MediaStore for Android 10+ or legacy method
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "QuizResult_" + subjectName.replaceAll(" ", "_") + "_" + timestamp + ".pdf";
            
            boolean success = savePdfDocument(context, document, fileName, "QuizMaster");
            
            if (success) {
                Toast.makeText(context, "PDF saved to Downloads/QuizMaster", Toast.LENGTH_LONG).show();
            }
            
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private static void drawHeader(Canvas canvas, Paint paint, String studentName, String subjectName,
                                   int semester, String level, int score, int totalQuestions, double percentage) {
        // Title
        paint.setColor(Color.rgb(98, 0, 238)); // Primary color
        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Quiz Result Report", 297, 50, paint);

        // Student details
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        
        canvas.drawText("Student Name: " + studentName, 40, 90, paint);
        canvas.drawText("Subject: " + subjectName, 40, 110, paint);
        canvas.drawText("Semester: " + semester, 40, 130, paint);
        canvas.drawText("Difficulty Level: " + level, 300, 130, paint);
        
        // Score box
        paint.setColor(Color.rgb(98, 0, 238));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(40, 145, 555, 185, paint);
        
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Score: " + score + "/" + totalQuestions, 50, 165, paint);
        canvas.drawText("Percentage: " + String.format("%.2f", percentage) + "%", 250, 165, paint);
        
        String status = percentage >= 40 ? "PASS" : "FAIL";
        paint.setColor(percentage >= 40 ? Color.rgb(0, 128, 0) : Color.rgb(255, 0, 0));
        canvas.drawText("Status: " + status, 450, 165, paint);
    }

    /**
     * Generate simple certificate PDF (for passing students)
     */
    public static boolean generateCertificate(Context context, String studentName, String subjectName,
                                             int score, int totalQuestions, double percentage) {
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // Border
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(Color.rgb(98, 0, 238));
            canvas.drawRect(20, 20, 575, 822, paint);

            // Title
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(98, 0, 238));
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("CERTIFICATE", 297, 100, paint);
            canvas.drawText("OF ACHIEVEMENT", 297, 150, paint);

            // Content
            paint.setTextSize(20);
            paint.setColor(Color.BLACK);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("This is to certify that", 297, 250, paint);

            paint.setTextSize(32);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(studentName, 297, 300, paint);

            paint.setTextSize(20);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("has successfully completed the quiz on", 297, 370, paint);

            paint.setTextSize(28);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(subjectName, 297, 420, paint);

            paint.setTextSize(22);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("with a score of " + score + "/" + totalQuestions, 297, 480, paint);
            canvas.drawText("(" + String.format("%.2f", percentage) + "%)", 297, 510, paint);

            // Date
            paint.setTextSize(16);
            paint.setColor(Color.GRAY);
            String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
            canvas.drawText("Date: " + date, 297, 700, paint);

            paint.setTextSize(14);
            canvas.drawText("Quiz Master Application", 297, 780, paint);

            document.finishPage(page);

            // Save certificate using MediaStore for Android 10+ or legacy method
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Certificate_" + subjectName.replaceAll(" ", "_") + "_" + timestamp + ".pdf";
            
            boolean success = savePdfDocument(context, document, fileName, "QuizMaster/Certificates");
            
            if (success) {
                Toast.makeText(context, "Certificate saved to Downloads/QuizMaster/Certificates", Toast.LENGTH_LONG).show();
            }
            
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating certificate: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    /**
     * Save PDF document using MediaStore API (Android 10+) or legacy method (older versions)
     */
    private static boolean savePdfDocument(Context context, PdfDocument document, String fileName, String subFolder) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+ (API 29+)
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + subFolder);
                
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                
                if (uri != null) {
                    OutputStream outputStream = resolver.openOutputStream(uri);
                    if (outputStream != null) {
                        document.writeTo(outputStream);
                        outputStream.close();
                        document.close();
                        return true;
                    }
                }
                document.close();
                return false;
                
            } else {
                // Legacy method for Android 9 and below
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File targetDir = new File(downloadsDir, subFolder);
                
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                
                File pdfFile = new File(targetDir, fileName);
                FileOutputStream fos = new FileOutputStream(pdfFile);
                document.writeTo(fos);
                fos.close();
                document.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                document.close();
            } catch (Exception ignored) {}
            return false;
        }
    }
}
