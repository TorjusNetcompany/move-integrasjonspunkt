package no.difi.meldingsutveksling.nextbest;

public enum ServiceBusQueueMode {
    INNSYN("innsyn"),
    DATA("data");

    private String fullname;

    ServiceBusQueueMode(String fullname) {
        this.fullname = fullname;
    }

    public String fullname() {
        return this.fullname;
    }
}
