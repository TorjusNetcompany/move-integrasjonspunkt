package no.difi.meldingsutveksling.validation.group;

import javax.validation.groups.Default;

public interface ValidationGroups {

    interface DocumentType extends Default {

        interface Arkivmelding extends DocumentType {
        }

        interface ArkivmeldingKvittering extends DocumentType {
        }

        interface Digital extends DocumentType {
        }

        interface Print extends DocumentType {
        }

        interface Innsynskrav extends DocumentType {
        }

        interface Publisering extends DocumentType {
        }

        interface EInnsynKvittering extends DocumentType {
        }
    }
}
