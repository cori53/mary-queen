MARY Android v3 SMART NATIVE

- Base web aggiornata a Mary v6 SMART
- Ciclo automatico e sintomi a tocco inclusi
- Bridge Android per notifiche native
- Android 13+: richiesta permesso POST_NOTIFICATIONS
- Gli appuntamenti con DATA + ORA impostano un promemoria Android
- versionCode 2 / versionName 1.1

TEST PRIMA DI GOOGLE PLAY:
1. Apri questo progetto in Android Studio.
2. Attendi Gradle Sync.
3. Collega il Redmi e premi Run.
4. In Mary: Agenda > Notifiche Smart > Attiva notifiche.
5. Consenti le notifiche quando Android lo chiede: deve apparire una notifica di prova.
6. Aggiungi un appuntamento futuro con data e ora e verifica il promemoria.
7. Solo dopo il test genera un nuovo Android App Bundle firmato con lo STESSO keystore/alias mary.

Nota: il promemoria usa AlarmManager senza permesso di allarme esatto; Android può ritardarlo leggermente per risparmio energetico.
