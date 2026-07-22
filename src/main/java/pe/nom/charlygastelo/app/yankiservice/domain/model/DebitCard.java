package pe.nom.charlygastelo.app.yankiservice.domain.model;

public record DebitCard(
        String id,
        String cardNumber,
        String type,
        String status
) {

    public boolean isDebit() {
        return "PERSONAL".equalsIgnoreCase(type);
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}