package de.abiturplanung.gui.timeline;

//Wichtig: Bei einem Record erzeugt Java automatisch Konstruktor, Getter-artige Methoden, equals() und hashCode(). Die Komponenten müssen nicht implementiert werden.
// Ein record ist also im Grunde Javas kompakte Antwort auf: //„Ich brauche eine kleine Klasse, die hauptsächlich einige zusammengehörige Werte repräsentiert.“ Und unser KommissionsSchluessel ist dafür beinahe ein Lehrbuchbeispiel.

import de.abiturplanung.model.Pruefung;
import java.util.List;

public record KommissionsGruppe(String pruefer, String vorsitz, String schriftfuehrer, List<Pruefung> pruefungen) {



}
