package dummy;

import java.util.List;

public class InvoiceService {
    
    public void processBilling(List<Invoice> invoices) {
        // A loop iterating over invoices and forcing lazy loading.
        for (Invoice invoice : invoices) {
            invoice.getLineItems(); 
        }
    }
}