package Stockly_Ecommerce.API.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {

    @Column(name = "delivery_postal_code", length = 9)
    private String postalCode;

    @Column(name = "delivery_street", length = 255)
    private String street;

    @Column(name = "delivery_number", length = 30)
    private String number;

    @Column(name = "delivery_complement", length = 120)
    private String complement;

    @Column(name = "delivery_neighborhood", length = 120)
    private String neighborhood;

    @Column(name = "delivery_city", length = 120)
    private String city;

    @Column(name = "delivery_state", length = 2)
    private String state;
}
