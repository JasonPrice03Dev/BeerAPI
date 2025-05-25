package src.application.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import src.application.DTO.BreweryInsightsDTO;
import src.application.exception.BreweryNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ReportGeneratorService {

    // Generates a PDF report of brewery details
    public byte[] createBreweryPDFReport(BreweryInsightsDTO breweryData) {
        if (breweryData == null || breweryData.getName() == null || breweryData.getLocation() == null) {
            throw new BreweryNotFoundException("Missing or invalid brewery data.");
        }

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Creating a content stream for writing text to the PDF
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.setLeading(20f);
                contentStream.newLineAtOffset(220, 780);
                contentStream.showText("Brewery Report Summary");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(40, 730);
                contentStream.setLeading(16f);
                contentStream.showText("Brewery Name: " + breweryData.getName());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("Location: " + breweryData.getLocation());
                contentStream.newLine();
                contentStream.showText("Contact Info: " + breweryData.getContact());
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Geolocation Data:");
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if (breweryData.getLatitude() != null && breweryData.getLongitude() != null) {
                    contentStream.showText("Latitude: " + breweryData.getLatitude() + ", Longitude: " + breweryData.getLongitude());
                } else {
                    contentStream.showText("Geolocation data unavailable.");
                }
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Customer Feedback Summary:");
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if (breweryData.getNumberOfReviews() > 0) {
                    contentStream.showText("Average Rating: " + breweryData.getAvgRating());
                    contentStream.newLine();
                    contentStream.showText("Total Reviews: " + breweryData.getNumberOfReviews());
                } else {
                    contentStream.showText("No customer reviews available.");
                }
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Brewery Beers Overview:");
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if (breweryData.getBeers().isEmpty()) {
                    contentStream.showText("No beers have been reviewed yet.");
                } else {
                    for (BreweryInsightsDTO.BeerInfo beer : breweryData.getBeers()) {
                        contentStream.showText("- " + beer.getBeerName() + " (" + beer.getStyle() + ", " + beer.getAbv() + "% ABV)");
                        contentStream.newLine();
                    }
                }

                contentStream.endText();
            }

            // Save the PDF document to a byte array in order to return it
            document.save(pdfOutputStream);
            return pdfOutputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("PDF report generation failed", e);
        }
    }
}
