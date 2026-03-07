package dummy;

import java.util.List;

public class ComprehensiveService {

    public void shouldFlagCollectionReturn(List<Invoice> invoices) {
        for (Invoice inv : invoices) {
            inv.getLineItems(); // Line 9 - True Positive (Collection return likely to cause N+1)
        }
    }

    public void shouldFlagEntityReturn(List<Invoice> invoices) {
        for (Invoice inv : invoices) {
            inv.getCustomer(); // Line 15 - True Positive (Entity return likely to cause N+1)
        }
    }

    public void shouldIgnoreBasicTypeReturn(List<Invoice> invoices) {
        for (Invoice inv : invoices) {
            inv.getName(); // Linhe 21 - False Positive (Basic type return does not cause N+1)
        }
    }

    public void shouldIgnoreDto(List<InvoiceDTO> dtos) {
        for (InvoiceDTO dto : dtos) {
            dto.getLineItems(); // Line 27 - False Positive (DTO return does not cause N+1)
        }
    }
}