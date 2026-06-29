package pe.nom.charlygastelo.app.yankiservice.domain.model;

public record Card(
        String id,
        String customerId,
        String accountId,
        String cardNumber,
        String type,
        String status
) {

    public boolean isDebit() {
        return "DEBIT".equalsIgnoreCase(type);
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}