package dummy;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Invoice {
    public List<String> getLineItems() { return null; }
    public Customer getCustomer() { return null; }
    public String getName() { return null; }
}