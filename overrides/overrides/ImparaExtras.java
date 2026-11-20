package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

/** Módulo extra 100% offline: 50 verbos, falsos amigos, palabra del día, shadowing y respaldo JSON. */
public final class ImparaExtras {
 private static final String DAILY_DONE_KEY = "impara_daily_done_v1";

 // Palabras del día (rotan automáticamente según el día del año)
 private static final String[][] DAILY_WORDS = {
  {"Magari", "¡Ojalá! / Ya me gustaría", "Magari potessi andare in Italia quest'anno!"},
  {"Allora", "Entonces / Bueno / Pues", "Allora, da dove cominciamo oggi?"},
  {"Comunque", "De todas formas / Sin embargo", "È tardi, ma comunque voglio studiare un po'."},
  {"Appena", "Apenas / En cuanto", "Chiamami appena arrivi a casa."},
  {"Abbastanza", "Bastante / Suficiente", "Parlo abbastanza bene l'italiano."},
  {"Purtroppo", "Lamentablemente / Por desgracia", "Purtroppo oggi piove tutto il giorno."},
  {"Soprattutto", "Sobre todo / Especialmente", "Amo la cucina italiana, soprattutto la pasta."},
  {"Invece", "En cambio / Sin embargo", "Lui preferisce il tè, io invece bevo caffè."},
  {"Davvero", "De verdad / Realmente", "Davvero hai imparato così in fretta?"},
  {"Subito", "Enseguida / Inmediatamente", "Arrivo subito, dammi solo un minuto!"},
  {"Mica", "Para nada / Ni siquiera (enfático)", "Non è mica difficile come sembra."},
  {"Ormai", "Ya / A estas alturas", "Ormai capisco quasi tutto quello che dici."}
 };

 // 50 verbos italianos más usados: {infinitivo, español, presente, passato prossimo, imperfetto, futuro}
 private static final String[][] VERBS_50 = {
  {"essere", "ser / estar", "io sono, tu sei, lui è, noi siamo", "sono stato/a", "ero, eri, era", "sarò, sarai, sarà"},
  {"avere", "tener / haber", "io ho, tu hai, lui ha, noi abbiamo", "ho avuto", "avevo, avevi, aveva", "avrò, avrai, avrà"},
  {"fare", "hacer", "io faccio, tu fai, lui fa, noi facciamo", "ho fatto", "facevo, facevi, faceva", "farò, farai, farà"},
  {"andare", "ir", "io vado, tu vai, lui va, noi andiamo", "sono andato/a", "andavo, andavi, andava", "andrò, andrai, andrà"},
  {"potere", "poder", "io posso, tu puoi, lui può, noi possiamo", "ho potuto", "potevo, potevi, poteva", "potrò, potrai, potrà"},
  {"volere", "querer", "io voglio, tu vuoi, lui vuole, noi vogliamo", "ho voluto", "volevo, volevi, voleva", "vorrò, vorrai, vorrà"},
  {"dovere", "deber / tener que", "io devo, tu devi, lui deve, noi dobbiamo", "ho dovuto", "dovevo, dovevi, doveva", "dovrò, dovrai, dovrà"},
  {"dire", "decir", "io dico, tu dici, lui dice, noi diciamo", "ho detto", "dicevo, dicevi, diceva", "dirò, dirai, dirà"},
  {"sapere", "saber", "io so, tu sai, lui sa, noi sappiamo", "ho saputo", "sapevo, sapevi, sapeva", "saprò, saprai, saprà"},
  {"vedere", "ver", "io vedo, tu vedi, lui vede, noi vediamo", "ho visto", "vedevo, vedevi, vedeva", "vedrò, vedrai, vedrà"},
  {"venire", "venir", "io vengo, tu vieni, lui viene, noi veniamo", "sono venuto/a", "venivo, venivi, veniva", "verrò, verrai, verrà"},
  {"stare", "estar / quedarse", "io sto, tu stai, lui sta, noi stiamo", "sono stato/a", "stavo, stavi, stava", "starò, starai, starà"},
  {"dare", "dar", "io do, tu dai, lui dà, noi diamo", "ho dato", "davo, davi, dava", "darò, darai, darà"},
  {"prendere", "tomar / coger", "io prendo, tu prendi, lui prende", "ho preso", "prendevo, prendevi", "prenderò, prenderai"},
  {"parlare", "hablar", "io parlo, tu parli, lui parla", "ho parlato", "parlavo, parlavi", "parlerò, parlerai"},
  {"mangiare", "comer", "io mangio, tu mangi, lui mangia", "ho mangiato", "mangiavo, mangiavi", "mangerò, mangerai"},
  {"bere", "beber", "io bevo, tu bevi, lui beve, noi beviamo", "ho bevuto", "bevevo, bevevi", "berrò, berrai, berrà"},
  {"dormire", "dormir", "io dormo, tu dormi, lui dorme", "ho dormito", "dormivo, dormivi", "dormirò, dormirai"},
  {"lavorare", "trabajar", "io lavoro, tu lavori, lui lavora", "ho lavorato", "lavoravo, lavoravi", "lavorerò, lavorerai"},
  {"studiare", "estudiar", "io studio, tu studi, lui studia", "ho studiato", "studiavo, studiavi", "studierò, studierai"},
  {"scrivere", "escribir", "io scrivo, tu scrivi, lui scrive", "ho scritto", "scrivevo, scrivevi", "scriverò, scriverai"},
  {"leggere", "leer", "io leggo, tu leggi, lui legge", "ho letto", "leggevo, leggevi", "leggerò, leggerai"},
  {"capire", "entender", "io capisco, tu capisci, lui capisce", "ho capito", "capivo, capivi", "capirò, capirai"},
  {"sentire", "oír / sentir", "io sento, tu senti, lui sente", "ho sentito", "sentivo, sentivi", "sentirò, sentirai"},
  {"aprire", "abrir", "io apro, tu apri, lui apre", "ho aperto", "aprivo, aprivi", "aprirò, aprirai"},
  {"chiudere", "cerrar", "io chiudo, tu chiudi, lui chiude", "ho chiuso", "chiudevo, chiudevi", "chiuderò, chiuderai"},
  {"partire", "partir / salir", "io parto, tu parti, lui parte", "sono partito/a", "partivo, partivi", "partirò, partirai"},
  {"arrivare", "llegar", "io arrivo, tu arrivi, lui arriva", "sono arrivato/a", "arrivavo, arrivavi", "arriverò, arriverai"},
  {"tornare", "volver", "io torno, tu torni, lui torna", "sono tornato/a", "tornavo, tornavi", "tornerò, tornerai"},
  {"uscire", "salir", "io esco, tu esci, lui esce, noi usciamo", "sono uscito/a", "uscivo, uscivi", "uscirò, uscirai"},
  {"entrare", "entrar", "io entro, tu entri, lui entra", "sono entrato/a", "entravo, entravi", "entrerò, entrerai"},
  {"aspettare", "esperar", "io aspetto, tu aspetti, lui aspetta", "ho aspettato", "aspettavo, aspettavi", "aspetterò, aspetterai"},
  {"cercare", "buscar", "io cerco, tu cerchi, lui cerca", "ho cercato", "cercavo, cercavi", "cercherò, cercherai"},
  {"trovare", "encontrar", "io trovo, tu trovi, lui trova", "ho trovato", "trovavo, trovavi", "troverò, troverai"},
  {"comprare", "comprar", "io compro, tu compri, lui compra", "ho comprato", "compravo, compravi", "comprerò, comprerai"},
  {"pagare", "pagar", "io pago, tu paghi, lui paga", "ho pagato", "pagavo, pagavi", "pagherò, pagherai"},
  {"cucinare", "cocinar", "io cucino, tu cucini, lui cucina", "ho cucinato", "cucinavo, cucinavi", "cucinerò, cucinerai"},
  {"giocare", "jugar", "io gioco, tu giochi, lui gioca", "ho giocato", "giocavo, giocavi", "giocherò, giocherai"},
  {"guardare", "mirar / ver", "io guardo, tu guardi, lui guarda", "ho guardato", "guardavo, guardavi", "guarderò, guarderai"},
  {"ascoltare", "escuchar", "io ascolto, tu ascolti, lui ascolta", "ho ascoltato", "ascoltavo, ascoltavi", "ascolterò, ascolterai"},
  {"chiedere", "preguntar / pedir", "io chiedo, tu chiedi, lui chiede", "ho chiesto", "chiedevo, chiedevi", "chiederò, chiederai"},
  {"rispondere", "responder", "io rispondo, tu rispondi, lui risponde", "ho risposto", "rispondevo, rispondevi", "risponderò, risponderai"},
  {"conoscere", "conocer", "io conosco, tu conosci, lui conosce", "ho conosciuto", "conoscevo, conoscevi", "conoscerò, conoscerai"},
  {"ricordare", "recordar", "io ricordo, tu ricordi, lui ricorda", "ho ricordato", "ricordavo, ricordavi", "ricorderò, ricorderai"},
  {"dimenticare", "olvidar", "io dimentico, tu dimentichi, lui dimentica", "ho dimenticato", "dimenticavo, dimenticavi", "dimenticherò, dimenticherai"},
  {"iniziare", "empezar", "io inizio, tu inizi, lui inizia", "ho iniziato", "iniziavo, iniziavi", "inizierò, inizierai"},
  {"finire", "terminar", "io finisco, tu finisci, lui finisce", "ho finito", "finivo, finivi", "finirò, finirai"},
  {"aiutare", "ayudar", "io aiuto, tu aiuti, lui aiuta", "ho aiutato", "aiutavo, aiutavi", "aiuterò, aiuterai"},
  {"amare", "amar", "io amo, tu ami, lui ama", "ho amato", "amavo, amavi", "amerò, amerai"},
  {"vivere", "vivir", "io vivo, tu vivi, lui vive", "ho vissuto", "vivevo, vivevi", "vivrò, vivrai"}
 };

 // Falsos amigos ES vs IT: {italiano, significado real, trampa en español, ejemplo IT}
 private static final String[][] FALSE_FRIENDS = {
  {"Burro", "Mantequilla", "NO es el animal (eso es 'asino')", "Metto il burro sul pane caldo."},
  {"Salire", "Subir", "NO es salir (eso es 'uscire')", "Saliamo al terzo piano a piedi."},
  {"Caldo", "Caliente / Calor", "NO es sopa (eso es 'brodo')", "Oggi fa molto caldo a Roma!"},
  {"Subito", "Enseguida / Ya", "NO es repentino (eso es 'improvviso')", "Torno subito, aspettami qui."},
  {"Camera", "Habitación / Cuarto", "NO es cámara de fotos ('macchina fotografica')", "Ho prenotato una camera doppia."},
  {"Largo", "Ancho", "NO es largo/extenso (eso es 'lungo')", "Questa strada è molto larga."},
  {"Guardare", "Mirar", "NO es guardar (eso es 'conservare / mettere via')", "Guarda che bel tramonto!"},
  {"Aceto", "Vinagre", "NO es aceite (eso es 'olio')", "Condisco l'insalata con olio e aceto."},
  {"Officina", "Taller mecánico", "NO es oficina (eso es 'ufficio')", "La mia auto è in officina."},
  {"Parenti", "Familiares / Parientes", "NO solo padres (padres es 'genitori')", "A Natale vedo tutti i miei parenti."},
  {"Topo", "Ratón", "NO es topo animal (eso es 'talpa')", "C'è un piccolo topo in giardino."},
  {"Imbarazzata", "Avergonzada / Incómoda", "NO es embarazada (eso es 'incinta')", "Ero un po' imbarazzata davanti a tutti."}
 };

 public static View buildDailyWordCard(MainActivity a, Runnable dismissHub) {
  int dayIdx = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % DAILY_WORDS.length;
  String[] w = DAILY_WORDS[dayIdx];
  String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
  boolean done = today.equals(a.prefs.getString(DAILY_DONE_KEY, ""));

  LinearLayout card = new LinearLayout(a);
  card.setOrientation(LinearLayout.VERTICAL);
  card.setPadding(a.dp(12), a.dp(10), a.dp(12), a.dp(10));
  card.setBackground(ImparaSuperApp.roundBg(a, ImparaSuperApp.bgBadge(a), ImparaSuperApp.accent(a), 16));

  TextView tag = new TextView(a);
  tag.setText(done ? "🌟 PALABRA DEL DÍA · COMPLETADA ✅" : "🌟 RETO DIARIO · PALABRA DEL DÍA (+20 XP)");
  tag.setTextColor(ImparaSuperApp.accent(a));
  tag.setTextSize(10.5f);
  tag.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
  card.addView(tag);

  TextView word = new TextView(a);
  word.setText(w[0] + " — " + w[1]);
  word.setTextColor(ImparaSuperApp.INK);
  word.setTextSize(16);
  word.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
  word.setPadding(0, a.dp(3), 0, a.dp(2));
  card.addView(word);

  TextView ex = new TextView(a);
  ex.setText("«" + w[2] + "»");
  ex.setTextColor(ImparaSuperApp.muted(a));
  ex.setTextSize(12.5f);
  ex.setPadding(0, 0, 0, a.dp(8));
  card.addView(ex);

  LinearLayout btnRow = new LinearLayout(a);
  btnRow.setOrientation(LinearLayout.HORIZONTAL);

  TextView bHear = chipBtn(a, "🔊 Escuchar", () -> a.speak(w[0] + ". " + w[2], 0, 0.92f));
  LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, -2, 1f);
  lp1.setMarginEnd(a.dp(6));
  btnRow.addView(bHear, lp1);

  TextView bDone = chipBtn(a, done ? "💬 Practicar" : "✅ Completar +20 XP", () -> {
   if (!done) {
    a.prefs.edit().putString(DAILY_DONE_KEY, today).apply();
    ImparaSuperApp.recordActivity(a, 20);
    a.toast("🎉 ¡Reto diario completado! +20 XP");
   }
   dismissHub.run();
   a.ask("Ciao Sofia! Fammi praticare la parola del giorno '" + w[0] + "' con un esempio semplice.");
  });
  btnRow.addView(bDone, new LinearLayout.LayoutParams(0, -2, 1f));

  card.addView(btnRow);
  return card;
 }

 private static TextView chipBtn(MainActivity a, String label, Runnable onClick) {
  TextView tv = new TextView(a);
  tv.setText(label);
  tv.setTextColor(ImparaSuperApp.INK);
  tv.setTextSize(12);
  tv.setGravity(Gravity.CENTER);
  tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
  tv.setPadding(a.dp(8), a.dp(7), a.dp(8), a.dp(7));
  tv.setBackground(ImparaSuperApp.roundBg(a, ImparaSuperApp.bgCard(a), 0x44FFFFFF, 10));
  tv.setOnClickListener(v -> onClick.run());
  return tv;
 }

 // 1. CONJUGADOR RÁPIDO DE 50 VERBOS
 public static void openVerbConjugator(MainActivity a) {
  LinearLayout root = ImparaSuperApp.dialogHeader(a, "50 VERBOS ESENCIALES · OFFLINE", "Conjugador rápido");
  EditText search = ImparaSuperApp.styledInput(a, "Buscar verbo (ej. essere, ir, comer)...", "");
  LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(-1, -2);
  sLp.setMargins(0, 0, 0, a.dp(8));
  root.addView(search, sLp);

  ScrollView scroll = new ScrollView(a);
  scroll.setVerticalScrollBarEnabled(false);
  LinearLayout list = new LinearLayout(a);
  list.setOrientation(LinearLayout.VERTICAL);
  scroll.addView(list);
  root.addView(scroll, new LinearLayout.LayoutParams(-1, a.dp(340)));

  Runnable render = () -> {
   list.removeAllViews();
   String q = search.getText().toString().trim().toLowerCase(Locale.ROOT);
   LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
   gap.setMargins(0, 0, 0, a.dp(6));
   for (String[] v : VERBS_50) {
    if (q.isEmpty() || v[0].contains(q) || v[1].toLowerCase(Locale.ROOT).contains(q)) {
     list.addView(ImparaSuperApp.buildOptionRow(a, "📚", v[0] + " (" + v[1] + ")",
      "Pres: " + v[2], () -> showVerbDetail(a, v)), gap);
    }
   }
  };
  render.run();

  search.addTextChangedListener(new TextWatcher() {
   public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
   public void onTextChanged(CharSequence s, int start, int before, int count) { render.run(); }
   public void afterTextChanged(Editable s) {}
  });

  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  ImparaSuperApp.styleDialogWindow(a, dialog, root);
 }

 private static void showVerbDetail(MainActivity a, String[] v) {
  ImparaSuperApp.recordActivity(a, 5);
  ScrollView scroll = new ScrollView(a);
  LinearLayout box = ImparaSuperApp.dialogHeader(a, "CONJUGACIÓN · TOCA PARA OÍR", v[0] + " (" + v[1] + ")");
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  box.addView(ImparaSuperApp.buildOptionRow(a, "🔊", "Presente", v[2], () -> a.speak(v[2], 0, 0.9f)), gap);
  box.addView(ImparaSuperApp.buildOptionRow(a, "🔊", "Passato Prossimo", "io " + v[3], () -> a.speak("io " + v[3], 0, 0.9f)), gap);
  box.addView(ImparaSuperApp.buildOptionRow(a, "🔊", "Imperfetto", v[4], () -> a.speak(v[4], 0, 0.9f)), gap);
  box.addView(ImparaSuperApp.buildOptionRow(a, "🔊", "Futuro Semplice", v[5], () -> a.speak(v[5], 0, 0.9f)), gap);

  scroll.addView(box);
  AlertDialog d = new AlertDialog.Builder(a)
   .setPositiveButton("Practicar con Sofía", (x, y) -> a.ask("Ciao Sofia! Fammi 3 frasi di pratica con il verbo '" + v[0] + "' in tempi diversi."))
   .setNegativeButton("Cerrar", null)
   .create();
  ImparaSuperApp.styleDialogWindow(a, d, scroll);
 }

 // 2. DETECTOR DE FALSOS AMIGOS (ES vs IT) + MINI-QUIZ
 public static void openFalseFriends(MainActivity a) {
  ScrollView scroll = new ScrollView(a);
  LinearLayout root = ImparaSuperApp.dialogHeader(a, "TRAMPAS ES vs IT · OFFLINE", "Falsos Amigos");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  root.addView(ImparaSuperApp.buildOptionRow(a, "🎯", "Iniciar Mini-Quiz de Falsos Amigos",
   "Pon a prueba tu reflejo (+10 XP por acierto)", () -> {
    if (holder[0] != null) holder[0].dismiss();
    runFalseFriendQuiz(a, 0, 0);
   }), gap);

  for (String[] f : FALSE_FRIENDS) {
   root.addView(ImparaSuperApp.buildOptionRow(a, "🚨", f[0] + " = " + f[1],
    f[2] + "\nEj: " + f[3], () -> {
     ImparaSuperApp.recordActivity(a, 3);
     a.speak(f[0] + ". " + f[3], 0, 0.9f);
    }), gap);
  }
  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  ImparaSuperApp.styleDialogWindow(a, dialog, scroll);
 }

 private static void runFalseFriendQuiz(MainActivity a, int idx, int score) {
  int total = 5;
  if (idx >= total) {
   ImparaSuperApp.recordActivity(a, score * 10);
   LinearLayout box = ImparaSuperApp.dialogHeader(a, "QUIZ TERMINADO", "Aciertos: " + score + " / " + total);
   TextView tv = new TextView(a);
   tv.setText("Ganaste +" + (score * 10) + " XP. ¡Ya no caerás en estas trampas!");
   tv.setTextColor(ImparaSuperApp.muted(a));
   box.addView(tv);
   ImparaSuperApp.styleDialogWindow(a, new AlertDialog.Builder(a).setPositiveButton("¡Genial!", null).create(), box);
   return;
  }
  Random r = new Random();
  int target = (idx * 2 + r.nextInt(2)) % FALSE_FRIENDS.length;
  String[] item = FALSE_FRIENDS[target];
  ArrayList<String> opts = new ArrayList<>();
  opts.add(item[1]);
  while (opts.size() < 3) {
   String cand = FALSE_FRIENDS[r.nextInt(FALSE_FRIENDS.length)][1];
   if (!opts.contains(cand)) opts.add(cand);
  }
  Collections.shuffle(opts);

  LinearLayout root = ImparaSuperApp.dialogHeader(a, "FALSOS AMIGOS · " + (idx + 1) + "/" + total,
   "¿Qué significa «" + item[0] + "» en italiano?");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (String opt : opts) {
   root.addView(ImparaSuperApp.buildOptionRow(a, "⚡", opt, "Toca para responder", () -> {
    if (holder[0] != null) holder[0].dismiss();
    boolean ok = opt.equals(item[1]);
    a.toast(ok ? "✅ ¡Correcto! " + item[0] + " = " + item[1] : "❌ Era: " + item[1]);
    runFalseFriendQuiz(a, idx + 1, score + (ok ? 1 : 0));
   }), gap);
  }
  AlertDialog d = new AlertDialog.Builder(a).setNegativeButton("Salir", null).create();
  holder[0] = d;
  ImparaSuperApp.styleDialogWindow(a, d, root);
 }

 // 3. MODO SHADOWING (NORMAL -> PAUSA -> LENTO 0.75x)
 public static void openShadowing(MainActivity a, String initialPhrase) {
  final String[] presets = {
   !TextUtils.isEmpty(initialPhrase) ? initialPhrase : "Vorrei un caffè macchiato e un cornetto, per favore.",
   "Scusa, puoi parlare un po' più lentamente?",
   "Mi piace molto imparare l'italiano ogni giorno.",
   "Che bella giornata per fare una passeggiata in centro!",
   "Non vedo l'ora di visitare Roma e Firenze."
  };

  ScrollView scroll = new ScrollView(a);
  LinearLayout root = ImparaSuperApp.dialogHeader(a, "ENTRENADOR DE ACENTO", "Modo Shadowing");
  TextView info = new TextView(a);
  info.setText("Sofía dirá la frase a velocidad normal (1.0x), hará una pausa para que repitas en voz alta y luego la repetirá lenta (0.75x).");
  info.setTextColor(ImparaSuperApp.muted(a));
  info.setTextSize(12.5f);
  info.setPadding(0, 0, 0, a.dp(8));
  root.addView(info);

  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (int i = 0; i < presets.length; i++) {
   final String phrase = presets[i];
   String label = (i == 0 && !TextUtils.isEmpty(initialPhrase)) ? "Frase del mensaje actual" : "Práctica #" + (i + 1);
   root.addView(ImparaSuperApp.buildOptionRow(a, "🎧", label, phrase, () -> startShadowingCycle(a, phrase)), gap);
  }

  scroll.addView(root);
  ImparaSuperApp.styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), scroll);
 }

 public static void startShadowingCycle(MainActivity a, String phrase) {
  ImparaSuperApp.recordActivity(a, 10);
  Handler h = new Handler(Looper.getMainLooper());
  a.toast("🎧 1/2 Velocidad normal — Escucha y repite...");
  a.speak(phrase, 0, 1.0f);
  long pauseMs = Math.max(3800L, Math.min(9000L, phrase.length() * 110L));
  h.postDelayed(() -> {
   a.toast("🐢 2/2 Velocidad lenta (0.75x) — Repite con Sofía");
   a.speak(phrase, 0, 0.75f);
  }, pauseMs);
 }

 // 4. RESPALDO DE PROGRESO (EXPORTAR / IMPORTAR JSON)
 public static void openBackupDialog(MainActivity a) {
  ScrollView scroll = new ScrollView(a);
  LinearLayout root = ImparaSuperApp.dialogHeader(a, "SEGURIDAD OFFLINE", "Respaldo de progreso (JSON)");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  root.addView(ImparaSuperApp.buildOptionRow(a, "📤", "Exportar todo al portapapeles",
   "Copia flashcards, XP, racha, nivel, tema e historial", () -> {
    try {
     JSONObject out = new JSONObject();
     out.put("v", 1);
     out.put("cards", a.prefs.getString("impara_cards_v1", "[]"));
     out.put("history", a.prefs.getString("impara_chat_history_v1", "[]"));
     out.put("xp", a.prefs.getInt("impara_xp_v1", 0));
     out.put("streak", a.prefs.getInt("impara_streak_v1", 1));
     out.put("last_day", a.prefs.getString("impara_last_day_v1", ""));
     out.put("level", a.prefs.getString("impara_level_v1", "A2"));
     out.put("theme", a.prefs.getInt("impara_theme_v1", 0));
     out.put("style", a.prefs.getString("impara_sofia_style_v1", "Profesora atractiva y cercana"));
     ClipboardManager cm = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
     if (cm != null) {
      cm.setPrimaryClip(ClipData.newPlainText("ImparaBackup", out.toString()));
      a.toast("✅ Respaldo copiado. Guárdalo en tus notas o WhatsApp.");
     }
    } catch (Exception e) {
     a.toast("Error exportando respaldo");
    }
   }), gap);

  root.addView(ImparaSuperApp.buildOptionRow(a, "📥", "Importar respaldo JSON",
   "Pega aquí tu código guardado para restaurar todo", () -> {
    if (holder[0] != null) holder[0].dismiss();
    LinearLayout box = ImparaSuperApp.dialogHeader(a, "RESTAURAR DATOS", "Pega tu respaldo JSON");
    EditText et = ImparaSuperApp.styledInput(a, "{\"v\":1,...}", "");
    box.addView(et);
    AlertDialog d2 = new AlertDialog.Builder(a)
     .setPositiveButton("Restaurar", (x, y) -> {
      try {
       JSONObject in = new JSONObject(et.getText().toString().trim());
       a.prefs.edit()
        .putString("impara_cards_v1", in.optString("cards", "[]"))
        .putString("impara_chat_history_v1", in.optString("history", "[]"))
        .putInt("impara_xp_v1", in.optInt("xp", 0))
        .putInt("impara_streak_v1", in.optInt("streak", 1))
        .putString("impara_last_day_v1", in.optString("last_day", ""))
        .putString("impara_level_v1", in.optString("level", "A2"))
        .putInt("impara_theme_v1", in.optInt("theme", 0))
        .putString("impara_sofia_style_v1", in.optString("style", "Profesora atractiva y cercana"))
        .apply();
       a.toast("✅ Progreso restaurado con éxito");
      } catch (Exception e) {
       a.toast("❌ JSON inválido. Verifica que lo copiaste completo.");
      }
     })
     .setNegativeButton("Cancelar", null)
     .create();
    ImparaSuperApp.styleDialogWindow(a, d2, box);
   }), new LinearLayout.LayoutParams(-1, -2));

  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  ImparaSuperApp.styleDialogWindow(a, dialog, scroll);
 }
}
