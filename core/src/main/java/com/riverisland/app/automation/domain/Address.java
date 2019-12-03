package com.riverisland.app.automation.domain;

import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.Region;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Random;

/**
 * Created by Prashant Ramcharan on 21/03/2017
 */
public class Address {
    private Region region;
    private DeliveryType deliveryType;
    private String telephone;
    private String addressLine1;
    private String townOrCity;
    private String postCode;
    private String country;
    private String recipientFirstName;
    private String recipientLastName;
    private Boolean addressLookup;

    public Address(Region region,
                   DeliveryType deliveryType,
                   String telephone,
                   String addressLine1,
                   String townOrCity,
                   String postCode,
                   String country,
                   String recipientFirstName,
                   String recipientLastName,
                   Boolean addressLookup) {
        this.region = region;
        this.deliveryType = deliveryType;
        this.telephone = telephone;
        this.addressLine1 = addressLine1;
        this.townOrCity = townOrCity;
        this.postCode = postCode;
        this.country = country;
        this.recipientFirstName = recipientFirstName;
        this.recipientLastName = recipientLastName;
        this.addressLookup = addressLookup;
    }

    public Address(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public Address(String addressLine1, Region region) {
        this.addressLine1 = addressLine1;
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getTownOrCity() {
        return townOrCity;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCountry() {
        return country;
    }

    public String getRecipientFirstName() {
        return recipientFirstName;
    }

    public String getRecipientLastName() {
        return recipientLastName;
    }

    public Boolean getAddressLookup() {
        return addressLookup == null ? Boolean.FALSE : addressLookup;
    }

    public String toIosFormattedAddress() {
        return String.format("%s %s %s %s %s %s", recipientFirstName, recipientLastName, addressLine1, townOrCity, postCode, country).trim();
    }

    public static class Builder {
        private Region region;
        private DeliveryType deliveryType;
        private String telephone;
        private String addressLine1;
        private String townOrCity;
        private String postCode;
        private String country;
        private String recipientFirstName;
        private String recipientLastName;
        private Boolean addressLookup;

        private Builder(Region region, DeliveryType deliveryType) {
            this.region = region;
            this.deliveryType = deliveryType;
        }

        public static Builder create() {
            return new Builder(null, null);
        }

        public static Builder create(Region region, DeliveryType deliveryType) {
            return new Builder(region, deliveryType);
        }

        public Builder withRegion(Region region) {
            this.region = region;
            return this;
        }

        public Builder withTelephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Builder withAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public Builder withTownOrCity(String townOrCity) {
            this.townOrCity = townOrCity;
            return this;
        }

        public Builder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withRecipientFirstName(String recepientFirstName) {
            this.recipientFirstName = recepientFirstName;
            return this;
        }

        public Builder withRecipientLastName(String recepientLastName) {
            this.recipientLastName = recepientLastName;
            return this;
        }

        public Builder withAddressLookup(Boolean addressLookup) {
            this.addressLookup = addressLookup;
            return this;
        }

        public Address build() {
            return new Address(region, deliveryType, telephone, addressLine1, townOrCity, postCode, country, recipientFirstName, recipientLastName, addressLookup);
        }

        public Address buildFromRegion(Region region) {
            withRegion(region);
            withTelephone("07912345678");
            withRecipientFirstName("James");
            withRecipientLastName("Smith");

            switch (region) {
                case GB:
                    withAddressLine1("Chelsea House")
                            .withTownOrCity("London")
                            .withPostCode("W5 1DR")
                            .withCountry("United Kingdom");
                    break;

                case AU:
                    withAddressLine1("1 Australia Road")
                            .withTownOrCity("Perth")
                            .withPostCode("WA 6009")
                            .withCountry("Australia");
                    break;

                case FR:
                    withAddressLine1("1 Paris Road")
                            .withTownOrCity("Paris")
                            .withPostCode("75008")
                            .withCountry("France");
                    break;

                case NL:
                    withAddressLine1("1 Amsterdam Road")
                            .withTownOrCity("Amsterdam")
                            .withPostCode("1071 JA")
                            .withCountry("Netherlands");
                    break;

                case SE:
                    withAddressLine1("1 Stockholm Road")
                            .withTownOrCity("Stockholm")
                            .withPostCode("113 90")
                            .withCountry("Sweden");
                    break;

                case DE:
                    withAddressLine1("1 Berlin Road")
                            .withTownOrCity("Berlin")
                            .withPostCode("10243")
                            .withCountry("Germany");
                    break;

                case US:
                    withAddressLine1("1600 Pennsylvania Ave")
                            .withTownOrCity("Washington")
                            .withPostCode("20500")
                            .withCountry("United States of America");
                    break;

                case EU:
                    withAddressLine1("El Numero 2920")
                            .withTownOrCity("Oderna")
                            .withPostCode("50026")
                            .withCountry("Spain");
                    break;
            }
            return build();
        }

        public Address buildForPreciseDayDelivery() {
            final List<String> areaPostcodes = Lists.newArrayList("B1 1DB");

            Random random = new Random();

            return withRegion(Region.GB)
                    .withTelephone("07912345678")
                    .withRecipientFirstName("James")
                    .withRecipientLastName("Smith")
                    .withAddressLine1("The Mailbox")
                    .withTownOrCity("Birmingham")
                    .withPostCode(areaPostcodes.get(random.nextInt(areaPostcodes.size())))
                    .withCountry("United Kingdom")
                    .build();
        }
    }
}
