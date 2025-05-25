package src.application.controller;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import src.application.DTO.BreweryInsightsDTO;
import src.application.DTO.CustomerBeerExperienceDTO;
import src.application.service.BreweryService;
import src.application.service.CustomerManagementService;
import src.application.service.ReportGeneratorService;

@RestController
@RequestMapping("/api")
@Tag(name = "Customer / Brewery", description = "Tasks related to customers and brewery")
@SecurityRequirement(name = "BearerAuth")
public class CustomerBreweryController {

    private static final Logger log = LoggerFactory.getLogger(CustomerBreweryController.class);
    private static final String TSV_MEDIA_TYPE = "text/tab-separated-values";
    private static final String YAML_MEDIA_TYPE = "application/x-yaml";

    // Handles customer operations such as retrieving beer experiences
    private final CustomerManagementService customerHandler;

    // Manages brewery data
    private final BreweryService breweryHandler;

    // Responsible for generating PDF reports for breweries
    private final ReportGeneratorService pdfGenerator;

    private final YAMLMapper yamlMapper = new YAMLMapper();

    public CustomerBreweryController(CustomerManagementService customerHandler, BreweryService breweryHandler, ReportGeneratorService pdfGenerator) {
        this.customerHandler = customerHandler;
        this.breweryHandler = breweryHandler;
        this.pdfGenerator = pdfGenerator;
    }

    // Retrieves an overview of a customer's beer experiences
    @Operation(summary = "Retrieve a customer's beer experiences", description = "Fetch the overview of a customer's beer experiences in different formats (JSON, XML ect.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer overview"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/customers/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, TSV_MEDIA_TYPE, YAML_MEDIA_TYPE})
    public ResponseEntity<?> fetchCustomerOverview(
            @PathVariable("id") Long customerId,
            @RequestHeader(HttpHeaders.ACCEPT) String format,
            Pageable pageable) {

        log.info("Retrieving overview for customer: {}", customerId);

        try {
            Page<CustomerBeerExperienceDTO> customerOverviewPage = customerHandler.fetchCustomerExperience(customerId, pageable);

            if (customerOverviewPage.isEmpty()) {
                log.warn("No data found for customer: {}", customerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            MediaType responseType = determineMediaType(format);
            Object responseBody;

            if (TSV_MEDIA_TYPE.equals(format)) {
                responseBody = customerHandler.toTSVFormat((CustomerBeerExperienceDTO) customerOverviewPage.getContent());
            } else if (YAML_MEDIA_TYPE.equals(format)) {
                responseBody = yamlMapper.writeValueAsString(customerOverviewPage.getContent());
            } else {
                responseBody = customerOverviewPage.getContent();
            }

            log.info("Returning {} format for customer: {}", responseType, customerId);
            return ResponseEntity.ok().contentType(responseType).body(responseBody);
        } catch (Exception e) {
            log.error("Error retrieving customer overview: {}", customerId, e);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    // Generates a brewery report in the form of a PDF
    @Operation(summary = "Generate a PDF brewery report", description = "Generate a PDF report for the specified brewery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated PDF report for brewery"),
            @ApiResponse(responseCode = "404", description = "Brewery not found"),
            @ApiResponse(responseCode = "500", description = "Error generating PDF report")
    })
    @GetMapping(value = "/breweries/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateBreweryPDF(@PathVariable("id") Long breweryId) {
        log.info("Creating brewery report for ID: {}", breweryId);

        BreweryInsightsDTO insights = breweryHandler.getBreweryReport(breweryId);

        if (insights == null) {
            log.error("Brewery report not found for ID: {}", breweryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            byte[] pdfData = pdfGenerator.createBreweryPDFReport(insights);
            log.info("Brewery report created for ID: {}", breweryId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=brewery_insights.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfData);

        } catch (Exception e) {
            log.error("Failed to generate report for brewery: {}", breweryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Determines the appropriate media type based on the request's Accept header
    private MediaType determineMediaType(String format) {
        if (MediaType.APPLICATION_XML_VALUE.equals(format)) {
            return MediaType.APPLICATION_XML;
        } else if (TSV_MEDIA_TYPE.equals(format)) {
            return MediaType.valueOf(TSV_MEDIA_TYPE);
        } else if (YAML_MEDIA_TYPE.equals(format)) {
            return MediaType.valueOf(YAML_MEDIA_TYPE);
        }
        return MediaType.APPLICATION_JSON;
    }
}
