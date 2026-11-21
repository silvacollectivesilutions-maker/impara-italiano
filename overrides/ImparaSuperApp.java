package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;

public final class ImparaSuperApp {
  static final int INK = 0xFFFFFFFF;
  static final int STROKE_SUBTLE = 0x33FFFFFF;

  private static final int[][] THEMES = {
    {0xFF101A2B, 0xFF17243B, 0xFF1E3252, 0xFF56B4FF, 0xFF9FB2C8},
    {0xFF1B1411, 0xFF281E19, 0xFF3A2A22, 0xFFE5A95C, 0xFFC7B2A3},
    {0xFF0E1D17, 0xFF152C23, 0xFF1C3D30, 0xFF4ADE80, 0xFFA3C7B5},
    {0xFF000000, 0xFF111318, 0xFF1B1F2A, 0xFF38BDF8, 0xFF94A3B8}
  };
  private static final String[] THEME_NAMES = {"Noche Azul", "Espresso Oscuro", "Verde Italia", "AMOLED Negro"};

  private static final String CARDS_KEY = "impara_cards_v1";
  private static final String XP_KEY = "impara_xp_v1";
  private static final String STREAK_KEY = "impara_streak_v1";
  private static final String LAST_DAY_KEY = "impara_last_day_v1";
  private static final String STYLE_KEY = "impara_sofia_style_v1";
  private static final String LEVEL_KEY = "impara_level_v1";
  private static final String THEME_KEY = "impara_theme_v1";

  private static final Pattern PAREN_PATTERN = Pattern.compile("\\([^()]*\\)");
  private static final String[] SPANISH_WORDS = {
    " que ", " qué ", " cómo ", " como ", " cuál ", " cual ", " quién ", " quien ",
    " por qué ", " porque ", " para ", " pero ", " puedes ", " quieres ", " tienes ",
    " estoy ", " estás ", " estas ", " está ", " esta ", " eres ", " muy ", " más ",
    " también ", " tambien ", " español ", " espanol ", " italiano ", " pregunta ",
    " respuesta ", " significa ", " explicar ", " explique ", " ejemplo ",
    " traducción ", " traduccion ", " escucha ", " repite ", " dime ", " cuéntame ",
    " cuentame ", " buenos días ", " buenos dias ", " buenas tardes ", " buenas noches ",
    " gracias ", " hola ", " hoy ", " aquí ", " aqui ", " allá ", " alla ", " dónde ",
    " donde ", " cuándo ", " cuando ", " cuánto ", " cuanto ", " necesito ", " hablar ",
    " decir ", " hacer ", " saber ", " pronunciar ", " mejor ", " los ", " las ",
    " el ", " del ", " dirías ", " dirias ", " día ", " dia "
  };

  private static int[] palette(MainActivity a) {
    int idx = (a != null && a.prefs != null) ? a.prefs.getInt(THEME_KEY, 0) : 0;
    if (idx < 0 || idx >= THEMES.length) idx = 0;
    return THEMES[idx];
  }

  static int bgSurface(MainActivity a) { return palette(a)[0]; }
  static int bgCard(MainActivity a) { return palette(a)[1]; }
  static int bgBadge(MainActivity a) { return palette(a)[2]; }
  static int accent(MainActivity a) { return palette(a)[3]; }
  static int muted(MainActivity a) { return palette(a)[4]; }

  static boolean isSpanishLine(String line) {
    if (TextUtils.isEmpty(line)) return false;
    String t = line.trim();
    String low = " " + t.toLowerCase(Locale.ROOT)
      .replaceAll("[.,;:!?\"'()\\[\\]«»]", " ")
      .replaceAll("\\s+", " ") + " ";

    if (t.contains("¿") || t.contains("¡") || t.contains("ñ") || t.contains("Ñ")
      || t.contains("á") || t.contains("Á") || t.contains("í") || t.contains("Í")
      || t.contains("ú") || t.contains("Ú")) {
      return true;
    }
    if (low.startsWith(" tu turno ") || low.startsWith(" traduccion ")
      || low.startsWith(" traducción ") || low.startsWith(" en espanol ")
      || low.startsWith(" en español ") || low.startsWith(" significado ")
      || low.startsWith(" explicacion ") || low.startsWith(" explicación ")) {
      return true;
    }
    int hits = 0;
    for (String w : SPANISH_WORDS) {
      if (low.contains(w)) {
        hits++;
        if (hits >= 2) return true;
      }
    }
    return false;
  }

  static String extractItalianOnly(String raw) {
    if (TextUtils.isEmpty(raw)) return "";
    String[] lines = raw.split("\\r?\\n");
    StringBuilder out = new StringBuilder();
    for (String line : lines) {
      String clean = line.trim();
      if (clean.isEmpty()) continue;

      Matcher m = PAREN_PATTERN.matcher(clean);
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        String grp = m.group();
        if (isSpanishLine(grp)) {
          m.appendReplacement(sb, "");
        } else {
          m.appendReplacement(sb, Matcher.quoteReplacement(grp));
        }
      }
      m.appendTail(sb);
      clean = sb.toString().trim();
      if (clean.isEmpty()) continue;

      if (!isSpanishLine(clean)) {
        if (out.length() > 0) out.append(". ");
        out.append(clean);
      }
    }
    return out.toString().trim();
  }

  public static void speakItalianOnly(MainActivity a, String raw, float rate) {
    try {
      if (a == null || TextUtils.isEmpty(raw)) return;
      String it = extractItalianOnly(raw);
      if (TextUtils.isEmpty(it)) {
        a.toast("Solo texto en español (Sofía solo pronuncia italiano)");
        return;
      }
      a.speak(it, 0, rate);
    } catch (Throwable ignored) {}
  }

  public static void startShadowingCycle(MainActivity a, String phrase) {
    try {
      String it = extractItalianOnly(phrase);
      if (TextUtils.isEmpty(it)) {
        a.toast("No hay frase en italiano para practicar");
        return;
      }
      recordActivity(a, 10);
      Handler h = new Handler(Looper.getMainLooper());
      a.toast("1/2 Normal — Escucha y repite...");
      a.speak(it, 0, 1.0f);
      long pauseMs = Math.max(3600L, Math.min(8500L, it.length() * 110L));
      h.postDelayed(() -> {
        try {
          a.toast("2/2 Lento (0.75x) — Repite con Sofía");
          a.speak(it, 0, 0.75f);
        } catch (Throwable ignored) {}
      }, pauseMs);
    } catch (Throwable ignored) {}
  }

  private static LinearLayout.LayoutParams rowGap(MainActivity a, int bottomDp) {
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(0, 0, 0, a.dp(bottomDp));
    return lp;
  }

  private static void safeRun(MainActivity a, Runnable action) {
    try {
      action.run();
    } catch (Throwable t) {
      try {
        a.toast("Aviso: " + t.getClass().getSimpleName());
      } catch (Throwable ignored) {}
    }
  }

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
    chip.setTextColor(accent(a));
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
    et.setHintTextColor(muted(a));
    et.setTextColor(INK);
    et.setPadding(a.dp(12), a.dp(10), a.dp(12), a.dp(10));
    et.setBackground(roundBg(a, bgCard(a), accent(a), 12));
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
    row.setBackground(roundBg(a, bgCard(a), STROKE_SUBTLE, 14));
    row.setClickable(true);
    row.setFocusable(true);
    row.setOnClickListener(v -> safeRun(a, action));

    TextView badge = new TextView(a);
    badge.setText(badgeText);
    badge.setTextColor(INK);
    badge.setTextSize(14);
    badge.setGravity(Gravity.CENTER);
    badge.setBackground(roundBg(a, bgBadge(a), (accent(a) & 0x00FFFFFF) | 0x44000000, 10));
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
      tvSub.setTextColor(muted(a));
      tvSub.setTextSize(11.5f);
      tvSub.setPadding(0, a.dp(1), 0, 0);
      col.addView(tvSub);
    }

    row.addView(col, new LinearLayout.LayoutParams(0, -2, 1f));

    TextView arrow = new TextView(a);
    arrow.setText("›");
    arrow.setTextColor(accent(a));
    arrow.setTextSize(18);
    arrow.setPadding(a.dp(6), 0, a.dp(2), 0);
    row.addView(arrow);
    return row;
  }

  static void styleDialogWindow(MainActivity a, AlertDialog dialog, View container) {
    dialog.setView(container, a.dp(14), a.dp(12), a.dp(14), a.dp(8));
    dialog.show();
    if (dialog.getWindow() != null) {
      dialog.getWindow().setBackgroundDrawable(roundBg(a, bgSurface(a), STROKE_SUBTLE, 22));
    }
    Button neg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
    if (neg != null) { neg.setTextColor(accent(a)); neg.setAllCaps(false); }
    Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    if (pos != null) { pos.setTextColor(INK); pos.setAllCaps(false); }
    Button neu = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
    if (neu != null) { neu.setTextColor(muted(a)); neu.setAllCaps(false); }
  }

  public static void saveFlashcardDirect(MainActivity a, String phrase) {
    if (TextUtils.isEmpty(phrase)) { a.toast("Mensaje vacío"); return; }
    try {
      JSONArray cards = new JSONArray(a.prefs.getString(CARDS_KEY, "[]"));
      JSONObject o = new JSONObject();
      o.put("it", phrase.trim());
      cards.put(o);
      a.prefs.edit().putString(CARDS_KEY, cards.toString()).apply();
      recordActivity(a, 10);
      a.toast("Guardado en Flashcards (+10 XP)");
    } catch (Exception e) {
      a.toast("No se pudo guardar");
    }
  }

  public static void openHub(MainActivity a, String selectedText) {
    Runnable launch = () -> safeRun(a, () -> openHubInternal(a, selectedText));
    if (a != null && a.root != null) a.root.post(launch);
    else launch.run();
  }

  private static void openHubInternal(MainActivity a, String selectedText) {
    recordActivity(a, 5);
    int xp = a.prefs != null ? a.prefs.getInt(XP_KEY, 0) : 0;
    int streak = a.prefs != null ? a.prefs.getInt(STREAK_KEY, 1) : 1;
    String level = a.prefs != null ? a.prefs.getString(LEVEL_KEY, "A2") : "A2";
    int themeIdx = a.prefs != null ? a.prefs.getInt(THEME_KEY, 0) : 0;
    String themeName = THEME_NAMES[Math.max(0, Math.min(themeIdx, THEME_NAMES.length - 1))];
    String style = a.prefs != null ? a.prefs.getString(STYLE_KEY, "Profesora italiana") : "Profesora italiana";

    ScrollView scroll = new ScrollView(a);
    scroll.setVerticalScrollBarEnabled(false);
    LinearLayout root = dialogHeader(a, "IMPARA L'ITALIANO · SÚPER APP", streak + "d · " + xp + " XP · Nivel " + level);

    final AlertDialog[] holder = new AlertDialog[1];
    Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

    root.addView(buildOptionRow(a, "▶", "Pronunciar solo italiano", "Omite líneas en español para sonar natural", () -> {
      dismiss.run();
      String target = !TextUtils.isEmpty(selectedText) ? selectedText : "Buongiorno! Come stai oggi?";
      speakItalianOnly(a, target, 0.95f);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "◎", "Modo Shadowing", "Italiano normal (1.0x), pausa y lento (0.75x)", () -> {
      dismiss.run();
      String target = !TextUtils.isEmpty(selectedText) ? selectedText : "Vorrei un caffè macchiato, per favore.";
      startShadowingCycle(a, target);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "✦", "Corrector de italiano", "Mejora tu frase con Sofía en italiano", () -> {
      dismiss.run();
      correctPhrase(a, selectedText);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "❖", "Escenarios de práctica", "Bar, hotel, charla o examen 1-100", () -> {
      dismiss.run();
      openRoleplay(a);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "♡", "Flashcards offline", "Guarda frases y repasa con audio", () -> {
      dismiss.run();
      openFlashcards(a, selectedText);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "⚡", "Frases rápidas en italiano", "Expresiones útiles con audio en 1 toque", () -> {
      dismiss.run();
      openQuickPhrases(a);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "≡", "Nivel de Sofía (" + level + ")", "Ajusta entre A1, A2, B1 y B2", () -> {
      dismiss.run();
      chooseLevel(a);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "◐", "Tema visual (" + themeName + ")", "Noche Azul, Espresso, Verde Italia, AMOLED", () -> {
      dismiss.run();
      chooseTheme(a);
    }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "★", "Estilo de Sofía", "Actual: " + style, () -> {
      dismiss.run();
      chooseSofiaStyle(a);
    }), rowGap(a, 0));

    scroll.addView(root);
    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    styleDialogWindow(a, dialog, scroll);
  }

  private static void chooseLevel(MainActivity a) {
    final String[] lvls = {"A1", "A2", "B1", "B2"};
    final String[] descs = {
      "Principiante: frases cortas y claras",
      "Elemental: conversación cotidiana sencilla",
      "Intermedio: tiempos pasados y futuro",
      "Avanzado: italiano fluido y natural"
    };
    final String[] prompts = {
      "Ciao Sofia! Parliamo solo in italiano a livello A1 con frasi molto brevi e chiare.",
      "Ciao Sofia! Parliamo solo in italiano a livello A2 con frasi semplici di tutti i giorni.",
      "Ciao Sofia! Parliamo solo in italiano a livello B1 in modo naturale.",
      "Ciao Sofia! Parliamo solo in italiano a livello B2 con ritmo naturale."
    };

    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "SELECTOR DE NIVEL", "Nivel de Sofía");
    final AlertDialog[] holder = new AlertDialog[1];

    for (int i = 0; i < lvls.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, lvls[i], "Nivel " + lvls[i], descs[i], () -> {
        if (holder[0] != null) holder[0].dismiss();
        a.prefs.edit().putString(LEVEL_KEY, lvls[idx]).apply();
        a.toast("Nivel: " + lvls[idx]);
        a.ask(prompts[idx]);
      }), rowGap(a, 7));
    }
    scroll.addView(root);
    AlertDialog d = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = d;
    styleDialogWindow(a, d, scroll);
  }

  private static void chooseTheme(MainActivity a) {
    final String[] subs = {
      "Azul profundo nocturno con acento celeste",
      "Café oscuro elegante con acento dorado",
      "Verde esmeralda oscuro estilo Italia",
      "Negro puro AMOLED para ahorrar batería"
    };
    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "APARIENCIA", "Elige un tema visual");
    final AlertDialog[] holder = new AlertDialog[1];

    for (int i = 0; i < THEME_NAMES.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, "◐", THEME_NAMES[i], subs[i], () -> {
        if (holder[0] != null) holder[0].dismiss();
        a.prefs.edit().putInt(THEME_KEY, idx).apply();
        if (a.chat != null) a.chat.setBackgroundColor(THEMES[idx][0]);
        a.toast("Tema: " + THEME_NAMES[idx]);
      }), rowGap(a, 7));
    }
    scroll.addView(root);
    AlertDialog d = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = d;
    styleDialogWindow(a, d, scroll);
  }

  private static void correctPhrase(MainActivity a, String initial) {
    LinearLayout box = dialogHeader(a, "CORRECTOR CON SOFÍA", "Mejorar frase en italiano");
    EditText input = styledInput(a, "Scrivi una frase in italiano...", initial);
    box.addView(input);

    AlertDialog dialog = new AlertDialog.Builder(a)
      .setPositiveButton("Corregir", (d, w) -> {
        String p = input.getText().toString().trim();
        if (!p.isEmpty()) {
          recordActivity(a, 15);
          a.ask("Rispondi solo in italiano: correggi e migliora questa frase in modo naturale:\n\"" + p + "\"");
        }
      })
      .setNegativeButton("Cancelar", null)
      .create();
    styleDialogWindow(a, dialog, box);
  }

  private static void openRoleplay(MainActivity a) {
    final String[] titles = {"Bar y restaurante", "Hotel y viaje", "Charla cotidiana", "Práctica con nota (1-100)"};
    final String[] subs = {"Pedir café y comida", "Llegada y direcciones", "Conversación natural", "3 preguntas y evaluación"};
    final String[] prompts = {
      "Ciao Sofia! Facciamo un roleplay solo in italiano in un bar: tu sei la barista e io ordino.",
      "Ciao Sofia! Simuliamo solo in italiano che sono arrivato in hotel a Roma.",
      "Ciao Sofia! Facciamo una chiacchierata naturale solo in italiano.",
      "Ciao Sofia! Fammi 3 domande brevi solo in italiano e alla fine dammi un voto da 1 a 100."
    };

    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "ROLEPLAY OFFLINE", "Elige un escenario");
    final AlertDialog[] holder = new AlertDialog[1];

    for (int i = 0; i < titles.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, "❖", titles[i], subs[i], () -> {
        if (holder[0] != null) holder[0].dismiss();
        recordActivity(a, 10);
        a.ask(prompts[idx]);
      }), rowGap(a, 7));
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

      root.addView(buildOptionRow(a, "+", "Guardar frase actual", "Añadir a tus tarjetas de repaso", () -> {
        dismiss.run();
        saveFlashcardDirect(a, !TextUtils.isEmpty(currentPhrase) ? currentPhrase : "Ciao! Come stai oggi?");
      }), rowGap(a, 7));

      for (int i = cards.length() - 1; i >= 0; i--) {
        final int idx = i;
        final String phrase = cards.getJSONObject(i).optString("it", "");
        root.addView(buildOptionRow(a, "♡", phrase, "Toca para escuchar o eliminar", () -> {
          dismiss.run();
          LinearLayout box = dialogHeader(a, "REPASO", "Tarjeta guardada");
          TextView tv = new TextView(a);
          tv.setText(phrase);
          tv.setTextColor(INK);
          tv.setTextSize(16);
          box.addView(tv);
          AlertDialog d2 = new AlertDialog.Builder(a)
            .setPositiveButton("▶ Normal", (x, y) -> speakItalianOnly(a, phrase, 1.0f))
            .setNeutralButton("▶ Lento", (x, y) -> speakItalianOnly(a, phrase, 0.78f))
            .setNegativeButton("Eliminar", (x, y) -> {
              cards.remove(idx);
              a.prefs.edit().putString(CARDS_KEY, cards.toString()).apply();
              a.toast("Eliminada");
            })
            .create();
          styleDialogWindow(a, d2, box);
        }), rowGap(a, 7));
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
      "Puoi ripetere più lentamente, per favore?",
      "Dov'è la stazione?",
      "Grazie mille!"
    };
    final String[] es = {
      "Buenos días / ¿Cómo estás?",
      "Un café cortado, por favor.",
      "¿Cuánto cuesta esto?",
      "¿Puedes repetir más despacio, por favor?",
      "¿Dónde está la estación?",
      "¡Muchas gracias!"
    };

    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "AUDIO EN ITALIANO", "Frases rápidas");

    for (int i = 0; i < it.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, "▶", it[i], es[i], () -> {
        recordActivity(a, 5);
        a.speak(it[idx], 0, 0.95f);
      }), rowGap(a, 7));
    }
    scroll.addView(root);
    styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), scroll);
  }

  private static void chooseSofiaStyle(MainActivity a) {
    final String[] styles = {"Profesora cercana", "Tutora paciente", "Profesora exigente", "Charla natural"};
    final String[] prompts = {
      "Ciao Sofia! Da ora in poi parlami solo in italiano come un'insegnante vicina e coinvolgente.",
      "Ciao Sofia! Da ora in poi parlami solo in italiano come un'insegnante paziente e chiara.",
      "Ciao Sofia! Da ora in poi parlami solo in italiano e correggi con precisione ogni mio errore.",
      "Ciao Sofia! Da ora in poi parliamo solo in italiano in modo naturale e colloquiale."
    };

    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "PERSONALIDAD DE SOFÍA", "Elige su estilo");
    final AlertDialog[] holder = new AlertDialog[1];

    for (int i = 0; i < styles.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, "★", styles[i], "Aplicar tono en el chat", () -> {
        if (holder[0] != null) holder[0].dismiss();
        a.prefs.edit().putString(STYLE_KEY, styles[idx]).apply();
        a.toast("Estilo: " + styles[idx]);
        a.ask(prompts[idx]);
      }), rowGap(a, 7));
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
