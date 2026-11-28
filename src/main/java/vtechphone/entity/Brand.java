package vtechphone.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}