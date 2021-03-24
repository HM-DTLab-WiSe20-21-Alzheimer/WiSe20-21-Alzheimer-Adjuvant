# Adjuvant

## Kurzbeschreibung
Ein einfach zu verwendendes, cloud-basiertes Modul für den Sprachassistenten "Amazon Alexa" zur Unterstützung von Alzheimerpatienten bei der Einhaltung von Terminen.

Das System läuft auf der Platform "Amazon Alexa" und speichert anstehende Termine des Patienten zur frühzeitigen späteren Erinnerung in der Cloud. Effiziente Routen zum gewünschten Standort werden mit Hilfe der [GeoCode Routing API](https://geocode.dev.stefan.zone) berechnet und an die in Alexa hinterlegte EMail Adresse gesendet.
Unter anderem kann man auch eigene Termine einer Kontakperson senden. Dies erfolgt ebenso per E-Mail, wobei die E-Mail Adresse der Kontaktperson mithilfe der Buchstabiertafel in DynamoDB abgespeichert wird.

**Hinweis:** Momentan handelt es sich bei Adjuvant um einen Prototypen!

## Dokumentation
Alle wichtigen Dokumente wie FAQs, Press Release, Benutzerkarte, Storyboard, [Präsentation](https://files.stefan.zone/software-engineering/documents/product_presentation.pdf) usw. befinden sich in dem Ordner `/documents`.

## Lust Adjuvant selbst zu nutzen?
Um Adjuvant selbst nutzen zu können wird ein Alexa Amazon Developer Account benötigt. Da sich das Projekt in der *development* Phase befindet, wird außerdem eine Einladung für das Projekt von einem Projektmitglied benötigt.

In dem Ordner `/config` befinden sich der Manifest des Skills und die schematische Definition des Interaktion-Modells. Mit diesen zwei Dateien könnte man diesen Skill jederzeit neu aufsetzen oder einen neuen gleichen Skill erstellen.

## Fehler gefunden? 👷‍

Vielen Dank für Ihre Nachricht! Bitte füllen Sie einen [Fehlerbericht](https://gitlab.lrz.de/stefankuehnel/sweng1-20-team11-adjuvant/-/issues/new) aus. Wir werden uns dann so schnell wie möglich um eine Lösung bemühen.

## Klonen des Projekts

```bash
git clone https://gitlab.lrz.de/stefankuehnel/sweng1-20-team11-adjuvant.git
```

## Lizenz
Dieses Projekt ist unter der MIT License lizensiert. Weitere Informationen finden Sie [hier](https://gitlab.lrz.de/stefankuehnel/sweng1-20-team11-adjuvant/-/blob/master/LICENSE).

## Mitwirkende

Team: Anonymous Student, Anonymous Student, [Stefan Kühnel](https://9bn.de/sk), Anonymous Student und Anonymous Student
