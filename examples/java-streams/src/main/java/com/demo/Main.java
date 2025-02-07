package com.demo;

import com.demo.processor.ProductEnrichmentProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ProductEnrichmentProcessor processor = new ProductEnrichmentProcessor();
        
        // Process different dataset sizes to demonstrate scalability
        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        
        for (int size : sizes) {
            log.info("Testing with dataset size: {}", size);
            processor.processLargeDataset(size);
            log.info("Completed processing {} items\n", size);
        }
    }
} 