package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

public final class ImparaSuperApp {
 static final int INK = 0xFFFFFFFF;
 static final int BG_SURFACE = 0xFF101A2B;
 static final int BG_CARD = 0xFF17243B;
 static final int BG_BADGE = 0xFF1E3252;
 static final int STROKE_SUBTLE = 0x33FFFFFF;
 static final int ACCENT = 0xFF56B4FF;
 static final int MUTED = 0xFF9FB2C8;

 private static final String CARDS_KEY = "impara_cards_v1";
 private static final String XP_KEY = "impara_xp_v1";
 private static final String STREAK_KEY = "impara_streak_v1";
 private static final String LAST_DAY_KEY = "impara_last_day_v1";
 private static final String STYLE_KEY = "impara_sofia_style_v1";

 static GradientDrawable roundBg(MainActivity a, int fill, int stroke, int radiusDp) {
  GradientDrawable g = new GradientDrawable();
  g.setShape(GradientDrawable.RECTANGLE);
  g.setColor(fill);
  g.setCornerRadius(a.dp(radiusDp));
  if (stroke != 0) g.setStroke(Math.max(1, a.dp(1)), stroke);
  return g;
 }

 static LinearLayout dialogHeader(MainActivity a, String chipText, String titleText) {
  LinearLayout root = new LinearLayout(a);
  root.setOrientation(LinearLayout.VERTICAL);

  TextView chip = new TextView(a);
  chip.setText(chipText);
  chip.setTextColor(ACCENT);
  chip.setTextSize(11);
  chip.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
  chip.setLetterSpacing(0.08f);
  root.addView(chip);

  TextView heading = new TextView(a);
  heading.setText(titleText);
  heading.setTextColor(INK);
  heading.setTextSize(18);
  heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
  heading.setPadding(0, a.dp(2), 0, a.dp(10));
  root.addView(heading);
  return root;
 }

 static EditText styledInput(MainActivity a, String hint, String initial) {
  EditText et = new EditText(a);
  et.setHint(hint);
  et.setHintTextColor(MUTED);
  et.setTextColor(INK);
  et.setPadding(a.dp(12), a.dp(10), a.dp(12), a.dp(10));
  et.setBackground(roundBg(a, BG_CARD, ACCENT, 12));
  if (!TextUtils.isEmpty(initial)) {
   et.setText(initial);
   et.setSelection(et.getText().length());
  }
  return et;
 }

 static View buildOptionRow(MainActivity a, String badgeText, String title, String subtitle, Runnable action) {
  LinearLayout row = new LinearLayout(a);
  row.setOrientation(LinearLayout.HORIZONTAL);
  row.setGravity(Gravity.CENTER_VERTICAL);
  row.setPadding(a.dp(10), a.dp(9), a.dp(10), a.dp(9));
  row.setBackground(roundBg(a, BG_CARD, STROKE_SUBTLE, 14));
  row.setClickable(true);
  row.setFocusable(true);
  row.setOnClickListener(v -> action.run());

  TextView badge = new TextView(a);
  badge.setText(badgeText);
  badge.setTextSize(14);
  badge.setGravity(Gravity.CENTER);
  badge.setBackground(roundBg(a, BG_BADGE, 0x4456B4FF, 10));
  LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(a.dp(34), a.dp(34));
  bLp.setMarginEnd(a.dp(10));
  row.addView(badge, bLp);

  LinearLayout col = new LinearLayout(a);
  col.setOrientation(LinearLayout.VERTICAL);

  TextView tvTitle = new TextView(a);
  tvTitle.setText(title);
  tvTitle.setTextColor(INK);
  tvTitle.setTextSize(14.5f);
  tvTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
  col.addView(tvTitle);

  if (!TextUtils.isEmpty(subtitle)) {
   TextView tvSub = new TextView(a);
   tvSub.setText(subtitle);
   tvSub.setTextColor(MUTED);
   tvSub.setTextSize(11.5f);
   tvSub.setPadding(0, a.dp(1), 0, 0);
   col.addView(tvSub);
  }

  row.addView(col, new LinearLayout.LayoutParams(0, -2, 1f));

  TextView arrow = new TextView(a);
  arrow.setText("›");
  arrow.setTextColor(ACCENT);
  arrow.setTextSize(18);
  arrow.setPadding(a.dp(6), 0, a.dp(2), 0);
  row.addView(arrow);
  return row;
 }

 static void styleDialogWindow(MainActivity a, AlertDialog dialog, View container) {
  dialog.setView(container, a.dp(14), a.dp(12), a.dp(14), a.dp(8));
  dialog.show();
  if (dialog.getWindow() != null) {
   dialog.getWindow().setBackgroundDrawable(roundBg(a, BG_SURFACE, STROKE_SUBTLE, 22));
  }
  Button neg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
  if (neg != null) { neg.setTextColor(ACCENT); neg.setAllCaps(false); }
  Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
  if (pos != null) { pos.setTextColor(INK); pos.setAllCaps(false); }
  Button neu = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
  if (neu != null) { neu.setTextColor(MUTED); neu.setAllCaps(false); }
 }

 public static void openHub(MainActivity a, String selectedText) {
  recordActivity(a, 5);
  int xp = a.prefs.getInt(XP_KEY, 0);
  int streak = a.prefs.getInt(STREAK_KEY, 1);
  String style = a.prefs.getString(STYLE_KEY, "Profesora atractiva y cercana");

  ScrollView scroll = new ScrollView(a);
  scroll.setVerticalScrollBarEnabled(false);
  LinearLayout root = dialogHeader(a, "IMPARA L'ITALIANO · SÚPER APP", "🔥 " + streak + "d · ⭐ " + xp + " XP");

  final AlertDialog[] holder = new AlertDialog[1];
  Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  root.addView(buildOptionRow(a, "✍️", "Corrector de italiano", "Analiza gramática y versión nativa", () -> {
   dismiss.run(); correctPhrase(a, selectedText);
  }), gap);

  root.addView(buildOptionRow(a, "🎭", "Escenarios y simulador", "Roleplay real o prueba con nota 1-100", () -> {
   dismiss.run(); openRoleplay(a);
  }), gap);

  root.addView(buildOptionRow(a, "🃏", "Flashcards offline", "Guarda frases y repasa con audio", () -> {
   dismiss.run(); openFlashcards(a, selectedText);
  }), gap);

  root.addView(buildOptionRow(a, "⚡", "Frases rápidas (1 toque)", "Reproduce o envía frases esenciales", () -> {
   dismiss.run(); openQuickPhrases(a);
  }), gap);

  root.addView(buildOptionRow(a, "🌐", "Modo intérprete (ES ↔ IT)", "Traduce y habla al instante", () -> {
   dismiss.run(); openInterpreter(a, selectedText);
  }), gap);

  root.addView(buildOptionRow(a, "🗣️", "Jerga y modismos reales", "Boh, magari, ci sta, figurati...", () -> {
   dismiss.run(); openSlang(a);
  }), gap);

  root.addView(buildOptionRow(a, "🎮", "Mini-juego de agilidad", "Quiz rápido offline de preposiciones", () -> {
   dismiss.run(); openMiniGame(a, 0, 0);
  }), gap);

  root.addView(buildOptionRow(a, "📖", "Historia interactiva", "Cuento en italiano + 3 preguntas", () -> {
   dismiss.run(); openStoryMode(a);
  }), gap);

  root.addView(buildOptionRow(a, "🏆", "Progreso, racha y nivel", "Ver estadísticas y reto diario", () -> {
   dismiss.run(); showStats(a);
  }), gap);

  root.addView(buildOptionRow(a, "👗", "Estilo y personalidad de Sofía", "Actual: " + style, () -> {
   dismiss.run(); chooseSofiaStyle(a);
  }), new LinearLayout.LayoutParams(-1, -2));

  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  styleDialogWindow(a, dialog, scroll);
 }

 private static void correctPhrase(MainActivity a, String initial) {
  LinearLayout box = dialogHeader(a, "CORRECTOR CON SOFÍA", "Mejorar frase en italiano");
  EditText input = styledInput(a, "Escribe o pega tu frase en italiano...", initial);
  box.addView(input);

  AlertDialog dialog = new AlertDialog.Builder(a)
   .setPositiveButton("Corregir", (d, w) -> {
    String p = input.getText().toString().trim();
    if (!p.isEmpty()) {
     recordActivity(a, 15);
     a.ask("Correggi questa frase in italiano, spiegami in spagnolo eventuali errori e dimmi come la direbbe un italiano:\n\"" + p + "\"");
    }
   })
   .setNegativeButton("Cancelar", null)
   .create();
  styleDialogWindow(a, dialog, box);
 }

 private static void openRoleplay(MainActivity a) {
  final String[] titles = {
   "Bar y restaurante", "Hotel y viaje", "Charla casual / cita",
   "Trabajo y entrevista", "Simulador con nota (1-100)"
  };
  final String[] subs = {
   "Pedir café, comida y cuenta", "Reservas, trenes y direcciones",
   "Conversación natural y coqueta", "Vocabulario profesional",
   "3 turnos y calificación final"
  };
  final String[] prompts = {
   "Ciao Sofia! Facciamo un roleplay in un bar italiano: tu sei la barista e io ordino.",
   "Ciao Sofia! Simuliamo che sono arrivato in hotel a Roma. Parlami in italiano.",
   "Ciao Sofia! Facciamo una chiacchierata rilassata e naturale come due amici a cena in Italia.",
   "Ciao Sofia! Simuliamo un breve colloquio di lavoro in italiano.",
   "Ciao Sofia! Mettimi alla prova con 3 domande brevi in italiano su una situazione reale e alla fine dammi un voto da 1 a 100 con 3 consigli."
  };

  ScrollView scroll = new ScrollView(a);
  LinearLayout root = dialogHeader(a, "ROLEPLAY OFFLINE", "Elige un escenario");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (int i = 0; i < titles.length; i++) {
   final int idx = i;
   root.addView(buildOptionRow(a, "🎭", titles[i], subs[i], () -> {
    if (holder[0] != null) holder[0].dismiss();
    recordActivity(a, 10);
    a.ask(prompts[idx]);
   }), gap);
  }
  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  styleDialogWindow(a, dialog, scroll);
 }

 private static void openFlashcards(MainActivity a, String currentPhrase) {
  try {
   JSONArray cards = new JSONArray(a.prefs.getString(CARDS_KEY, "[]"));
   ScrollView scroll = new ScrollView(a);
   LinearLayout root = dialogHeader(a, "CUADERNO OFFLINE", "Flashcards (" + cards.length() + ")");
   final AlertDialog[] holder = new AlertDialog[1];
   Runnable dismiss = () -> { if (holder[0] != null) holder[0].dismiss(); };
   LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
   gap.setMargins(0, 0, 0, a.dp(7));

   root.addView(buildOptionRow(a, "➕", "Guardar frase actual", "Añadir a tus tarjetas de repaso", () -> {
    dismiss.run();
    String base = !TextUtils.isEmpty(currentPhrase) ? currentPhrase : "Ciao! Come stai oggi?";
    try {
     JSONObject o = new JSONObject();
     o.put("it", base);
     cards.put(o);
     a.prefs.edit().putString(CARDS_KEY, cards.toString()).apply();
     recordActivity(a, 10);
     a.toast("Tarjeta guardada");
    } catch (Exception ignored) {}
   }), gap);

   for (int i = cards.length() - 1; i >= 0; i--) {
    final int idx = i;
    final String phrase = cards.getJSONObject(i).optString("it", "");
    root.addView(buildOptionRow(a, "🃏", phrase, "Toca para escuchar o eliminar", () -> {
     dismiss.run();
     LinearLayout box = dialogHeader(a, "REPASO", "Tarjeta guardada");
     TextView tv = new TextView(a);
     tv.setText(phrase);
     tv.setTextColor(INK);
     tv.setTextSize(16);
     box.addView(tv);
     AlertDialog d2 = new AlertDialog.Builder(a)
      .setPositiveButton("🔊 Normal", (x, y) -> a.speak(phrase, 0, 1.0f))
      .setNeutralButton("🐢 Lento", (x, y) -> a.speak(phrase, 0, 0.75f))
      .setNegativeButton("Eliminar", (x, y) -> {
       cards.remove(idx);
       a.prefs.edit().putString(CARDS_KEY, cards.toString()).apply();
       a.toast("Tarjeta eliminada");
      })
      .create();
     styleDialogWindow(a, d2, box);
    }), gap);
   }
   scroll.addView(root);
   AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
   holder[0] = dialog;
   styleDialogWindow(a, dialog, scroll);
  } catch (Exception e) {
   a.toast("Error en flashcards");
  }
 }

 private static void openQuickPhrases(MainActivity a) {
  final String[] it = {
   "Buongiorno! Come stai?",
   "Un caffè macchiato, per favore.",
   "Quanto costa questo?",
   "Non capisco, puoi ripetere più lentamente?",
   "Scusa, dov'è la stazione?",
   "Grazie mille, sei gentilissima!"
  };
  final String[] es = {
   "¡Buenos días! ¿Cómo estás?",
   "Un café cortado, por favor.",
   "¿Cuánto cuesta esto?",
   "No entiendo, ¿puedes repetir más despacio?",
   "Disculpa, ¿dónde está la estación?",
   "¡Muchas gracias, eres muy amable!"
  };

  ScrollView scroll = new ScrollView(a);
  LinearLayout root = dialogHeader(a, "AUDIO INMEDIATO", "Frases rápidas (1 toque)");
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (int i = 0; i < it.length; i++) {
   final int idx = i;
   root.addView(buildOptionRow(a, "🔊", it[i], es[i], () -> {
    recordActivity(a, 5);
    a.speak(it[idx], 0, 1.0f);
   }), gap);
  }
  scroll.addView(root);
  styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), scroll);
 }

 private static void openInterpreter(MainActivity a, String initial) {
  LinearLayout box = dialogHeader(a, "INTÉRPRETE ES ↔ IT", "Traduce y habla");
  EditText input = styledInput(a, "Escribe en español o italiano...", initial);
  box.addView(input);

  AlertDialog dialog = new AlertDialog.Builder(a)
   .setPositiveButton("A Italiano", (d, w) -> {
    String txt = input.getText().toString().trim();
    if (!txt.isEmpty()) {
     recordActivity(a, 10);
     a.ask("Traduci subito in italiano naturale questa frase e fammi sentire come si dice: " + txt);
    }
   })
   .setNeutralButton("🔊 Leer IT", (d, w) -> {
    String txt = input.getText().toString().trim();
    if (!txt.isEmpty()) a.speak(txt, 0, 1.0f);
   })
   .setNegativeButton("Cerrar", null)
   .create();
  styleDialogWindow(a, dialog, box);
 }

 private static void openSlang(MainActivity a) {
  final String[] terms = {"Boh!", "Magari!", "Ci sta", "In bocca al lupo!", "Figurati!", "Dai!"};
  final String[] defs = {
   "¡Quién sabe! / Ni idea",
   "¡Ojalá! / ¡Ya me gustaría!",
   "Tiene sentido / Me parece bien",
   "¡Mucha suerte! (se responde: Crepi!)",
   "¡Ni lo menciones! / De nada",
   "¡Vamos! / ¡Venga ya!"
  };

  ScrollView scroll = new ScrollView(a);
  LinearLayout root = dialogHeader(a, "ITALIANO REAL", "Jerga y modismos");
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (int i = 0; i < terms.length; i++) {
   final int idx = i;
   root.addView(buildOptionRow(a, "🗣️", terms[i], defs[i], () -> {
    recordActivity(a, 5);
    a.speak(terms[idx], 0, 0.9f);
   }), gap);
  }
  scroll.addView(root);
  styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), scroll);
 }

 private static void openMiniGame(MainActivity a, int qIdx, int score) {
  final String[][] qs = {
   {"Vado ___ Italia questa estate.", "in", "a", "da"},
   {"Abito ___ Roma da due anni.", "a", "in", "su"},
   {"Vengo ___ casa di Marco.", "da", "in", "per"},
   {"Io ___ un caffè ogni mattina.", "bevo", "bevi", "bere"}
  };
  if (qIdx >= qs.length) {
   recordActivity(a, score * 10);
   LinearLayout box = dialogHeader(a, "MINI-JUEGO TERMINADO", "Puntaje: " + score + " / " + qs.length);
   TextView tv = new TextView(a);
   tv.setText("Ganaste +" + (score * 10) + " XP. ¡Sigue practicando cada día!");
   tv.setTextColor(MUTED);
   box.addView(tv);
   styleDialogWindow(a, new AlertDialog.Builder(a).setPositiveButton("¡Genial!", null).create(), box);
   return;
  }

  String[] q = qs[qIdx];
  ArrayList<String> opts = new ArrayList<>(Arrays.asList(q[1], q[2], q[3]));
  Collections.shuffle(opts);

  LinearLayout root = dialogHeader(a, "RETO " + (qIdx + 1) + "/" + qs.length, q[0]);
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (String opt : opts) {
   root.addView(buildOptionRow(a, "🎯", opt, "Toca para responder", () -> {
    if (holder[0] != null) holder[0].dismiss();
    boolean ok = opt.equals(q[1]);
    a.toast(ok ? "✅ ¡Correcto!" : "❌ Era: " + q[1]);
    openMiniGame(a, qIdx + 1, score + (ok ? 1 : 0));
   }), gap);
  }
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Salir", null).create();
  holder[0] = dialog;
  styleDialogWindow(a, dialog, root);
 }

 private static void openStoryMode(MainActivity a) {
  final String[] genres = {"Misterio en Venecia", "Comedia en Roma", "Romance en Florencia", "Viaje en tren"};
  ScrollView scroll = new ScrollView(a);
  LinearLayout root = dialogHeader(a, "HISTORIA INTERACTIVA", "Elige un tema");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (String g : genres) {
   root.addView(buildOptionRow(a, "📖", g, "Cuento corto + 3 preguntas", () -> {
    if (holder[0] != null) holder[0].dismiss();
    recordActivity(a, 15);
    a.ask("Raccontami una storia breve e coinvolgente in italiano sul tema '" + g + "' (livello facile-intermedio) e alla fine fammi 3 domande di comprensione.");
   }), gap);
  }
  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  styleDialogWindow(a, dialog, scroll);
 }

 private static void showStats(MainActivity a) {
  int xp = a.prefs.getInt(XP_KEY, 0);
  int streak = a.prefs.getInt(STREAK_KEY, 1);
  String level = xp >= 500 ? "Avanzado (B2)" : (xp >= 250 ? "Intermedio (B1)" : (xp >= 100 ? "Elemental (A2)" : "Principiante (A1)"));

  LinearLayout box = dialogHeader(a, "TU PROGRESO", "Impara l'italiano");
  TextView tv = new TextView(a);
  tv.setText("🔥 Racha activa: " + streak + " día(s)\n⭐ Experiencia total: " + xp + " XP\n📈 Nivel estimado: " + level);
  tv.setTextColor(INK);
  tv.setTextSize(15);
  tv.setLineSpacing(a.dp(5), 1f);
  box.addView(tv);

  AlertDialog dialog = new AlertDialog.Builder(a)
   .setPositiveButton("Reto del día", (d, w) -> a.ask("Ciao Sofia! Dammi la sfida del giorno in italiano per il livello " + level + "."))
   .setNegativeButton("Cerrar", null)
   .create();
  styleDialogWindow(a, dialog, box);
 }

 private static void chooseSofiaStyle(MainActivity a) {
  final String[] styles = {
   "Profesora atractiva y cercana",
   "Tutora amigable y paciente",
   "Profesora estricta (corrige todo)",
   "Charla callejera con jerga"
  };
  final String[] prompts = {
   "Ciao Sofia! Da ora in poi comportati come una professoressa di italiano affascinante, sicura e molto vicina, rendendo la lezione divertente e coinvolgente.",
   "Ciao Sofia! Da ora in poi sii una tutor molto paziente e amichevole, spiegandomi tutto passo dopo passo.",
   "Ciao Sofia! Da ora in poi sii una professoressa severa e precisa: correggi ogni mio minimo errore di grammatica.",
   "Ciao Sofia! Da ora in poi parlami in modo super informale usando espressioni reali e slang italiano di tutti i giorni."
  };

  ScrollView scroll = new ScrollView(a);
  LinearLayout root = dialogHeader(a, "PERSONALIDAD DE SOFÍA", "Elige su estilo");
  final AlertDialog[] holder = new AlertDialog[1];
  LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
  gap.setMargins(0, 0, 0, a.dp(7));

  for (int i = 0; i < styles.length; i++) {
   final int idx = i;
   root.addView(buildOptionRow(a, "👗", styles[i], "Aplicar tono en el chat", () -> {
    if (holder[0] != null) holder[0].dismiss();
    a.prefs.edit().putString(STYLE_KEY, styles[idx]).apply();
    a.toast("Estilo activado: " + styles[idx]);
    a.ask(prompts[idx]);
   }), gap);
  }
  scroll.addView(root);
  AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
  holder[0] = dialog;
  styleDialogWindow(a, dialog, scroll);
 }

 public static void recordActivity(MainActivity a, int gainedXp) {
  if (a == null || a.prefs == null) return;
  SharedPreferences prefs = a.prefs;
  String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
  String lastDay = prefs.getString(LAST_DAY_KEY, "");
  int streak = prefs.getInt(STREAK_KEY, 1);
  if (!today.equals(lastDay)) {
   streak = lastDay.isEmpty() ? 1 : streak + 1;
   prefs.edit().putString(LAST_DAY_KEY, today).putInt(STREAK_KEY, streak).apply();
  }
  prefs.edit().putInt(XP_KEY, prefs.getInt(XP_KEY, 0) + Math.max(0, gainedXp)).apply();
 }
}
