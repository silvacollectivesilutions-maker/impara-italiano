package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;

/** Dashboard profesional con 3 pestanas, cuadriculas 2x2 y motor de audio 100% solo italiano. */
public final class ImparaSuperApp {
  static final int INK = 0xFFFFFFFF;
  static final int STROKE_SUBTLE = 0x26FFFFFF;

  private static final int[][] THEMES = {
    {0xFF0C1524, 0xFF132036, 0xFF1B2E4B, 0xFF4DA8FF, 0xFF94A8C0},
    {0xFF18120F, 0xFF241B16, 0xFF34261E, 0xFFE6A85C, 0xFFC4B0A1},
    {0xFF0C1914, 0xFF13271F, 0xFF1B382D, 0xFF4ADE80, 0xFF9FC4B2},
    {0xFF000000, 0xFF10131A, 0xFF191E29, 0xFF38BDF8, 0xFF94A3B8}
  };
  private static final String[] THEME_NAMES = {"Noche", "Espresso", "Italia", "AMOLED"};

  private static final String CARDS_KEY = "impara_cards_v1";
  private static final String HIST_KEY = "impara_chat_history_v1";
  private static final String XP_KEY = "impara_xp_v1";
  private static final String STREAK_KEY = "impara_streak_v1";
  private static final String LAST_DAY_KEY = "impara_last_day_v1";
  private static final String STYLE_KEY = "impara_sofia_style_v1";
  private static final String LEVEL_KEY = "impara_level_v1";
  private static final String THEME_KEY = "impara_theme_v1";
  private static final String DAILY_DONE_KEY = "impara_daily_done_v1";

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
    " el ", " del ", " dirías ", " dirias ", " día ", " dia ", " tal ", " tu ", " tus "
  };

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
    {"Mica", "Para nada / Ni siquiera", "Non è mica difficile come sembra."},
    {"Ormai", "Ya / A estas alturas", "Ormai capisco quasi tutto quello che dici."}
  };

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

  private static final String[][] FALSE_FRIENDS = {
    {"Burro", "Mantequilla", "NO es el animal (asino)", "Metto il burro sul pane caldo."},
    {"Salire", "Subir", "NO es salir (uscire)", "Saliamo al terzo piano a piedi."},
    {"Caldo", "Caliente / Calor", "NO es sopa (brodo)", "Oggi fa molto caldo a Roma!"},
    {"Subito", "Enseguida / Ya", "NO es repentino (improvviso)", "Torno subito, aspettami qui."},
    {"Camera", "Habitación", "NO es cámara (macchina fotografica)", "Ho prenotato una camera doppia."},
    {"Largo", "Ancho", "NO es largo (lungo)", "Questa strada è molto larga."},
    {"Guardare", "Mirar", "NO es guardar (conservare)", "Guarda che bel tramonto!"},
    {"Aceto", "Vinagre", "NO es aceite (olio)", "Condisco l'insalata con olio e aceto."},
    {"Officina", "Taller mecánico", "NO es oficina (ufficio)", "La mia auto è in officina."},
    {"Parenti", "Familiares", "NO solo padres (genitori)", "A Natale vedo tutti i miei parenti."},
    {"Topo", "Ratón", "NO es topo animal (talpa)", "C'è un piccolo topo in giardino."},
    {"Imbarazzata", "Avergonzada", "NO es embarazada (incinta)", "Ero un po' imbarazzata davanti a tutti."}
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

  /** Inserta un boton flotante elegante '✦ Estudio' sin tocar la barra de mensajes ni microfono. */
  public static void attachStudioFab(MainActivity a) {
    try {
      if (a == null) return;
      View contentView = a.findViewById(android.R.id.content);
      if (!(contentView instanceof FrameLayout)) return;
      FrameLayout content = (FrameLayout) contentView;
      View existing = content.findViewWithTag("impara_studio_fab");
      if (existing != null) {
        existing.setVisibility(a.page == 1 ? View.VISIBLE : View.GONE);
        return;
      }

      LinearLayout fab = new LinearLayout(a);
      fab.setTag("impara_studio_fab");
      fab.setOrientation(LinearLayout.HORIZONTAL);
      fab.setGravity(Gravity.CENTER_VERTICAL);
      fab.setPadding(a.dp(12), a.dp(8), a.dp(13), a.dp(8));
      fab.setBackground(roundBg(a, 0xE8132640, accent(a), 999));
      fab.setElevation(a.dp(6));
      fab.setClickable(true);
      fab.setFocusable(true);
      fab.setOnClickListener(v -> openHub(a, ""));

      TextView icon = new TextView(a);
      icon.setText("✦");
      icon.setTextColor(accent(a));
      icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
      icon.setPadding(0, 0, a.dp(5), 0);
      fab.addView(icon);

      TextView label = new TextView(a);
      label.setText("Estudio");
      label.setTextColor(INK);
      label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
      label.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
      fab.addView(label);

      FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.END);
      lp.setMargins(0, 0, a.dp(12), a.dp(158));
      content.addView(fab, lp);
    } catch (Throwable ignored) {}
  }

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
      clean = sb.toString().replaceAll("[\\p{So}\\p{Cn}]", "").trim();
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
    chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
    chip.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    chip.setLetterSpacing(0.08f);
    root.addView(chip);

    TextView heading = new TextView(a);
    heading.setText(titleText);
    heading.setTextColor(INK);
    heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
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

  static View buildGridTile(MainActivity a, String icon, String title, String sub, Runnable action) {
    LinearLayout tile = new LinearLayout(a);
    tile.setOrientation(LinearLayout.VERTICAL);
    tile.setPadding(a.dp(11), a.dp(11), a.dp(11), a.dp(11));
    tile.setBackground(roundBg(a, bgCard(a), STROKE_SUBTLE, 16));
    tile.setClickable(true);
    tile.setFocusable(true);
    tile.setOnClickListener(v -> safeRun(a, action));

    TextView badge = new TextView(a);
    badge.setText(icon);
    badge.setTextColor(accent(a));
    badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
    badge.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    badge.setGravity(Gravity.CENTER);
    badge.setBackground(roundBg(a, bgBadge(a), 0, 10));
    LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(a.dp(32), a.dp(32));
    bLp.setMargins(0, 0, 0, a.dp(8));
    tile.addView(badge, bLp);

    TextView tvTitle = new TextView(a);
    tvTitle.setText(title);
    tvTitle.setTextColor(INK);
    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
    tvTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    tvTitle.setMaxLines(1);
    tvTitle.setEllipsize(TextUtils.TruncateAt.END);
    tile.addView(tvTitle);

    if (!TextUtils.isEmpty(sub)) {
      TextView tvSub = new TextView(a);
      tvSub.setText(sub);
      tvSub.setTextColor(muted(a));
      tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
      tvSub.setMaxLines(2);
      tvSub.setEllipsize(TextUtils.TruncateAt.END);
      tvSub.setPadding(0, a.dp(2), 0, 0);
      tile.addView(tvSub);
    }
    return tile;
  }

  static LinearLayout buildTwoColRow(MainActivity a, View left, View right) {
    LinearLayout row = new LinearLayout(a);
    row.setOrientation(LinearLayout.HORIZONTAL);
    LinearLayout.LayoutParams lpL = new LinearLayout.LayoutParams(0, -1, 1f);
    lpL.setMarginEnd(a.dp(4));
    LinearLayout.LayoutParams lpR = new LinearLayout.LayoutParams(0, -1, 1f);
    lpR.setMarginStart(a.dp(4));
    row.addView(left, lpL);
    row.addView(right, lpR);
    return row;
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
    badge.setTextColor(accent(a));
    badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
    badge.setGravity(Gravity.CENTER);
    badge.setBackground(roundBg(a, bgBadge(a), 0, 10));
    LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(a.dp(32), a.dp(32));
    bLp.setMarginEnd(a.dp(10));
    row.addView(badge, bLp);

    LinearLayout col = new LinearLayout(a);
    col.setOrientation(LinearLayout.VERTICAL);

    TextView tvTitle = new TextView(a);
    tvTitle.setText(title);
    tvTitle.setTextColor(INK);
    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
    tvTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
    col.addView(tvTitle);

    if (!TextUtils.isEmpty(subtitle)) {
      TextView tvSub = new TextView(a);
      tvSub.setText(subtitle);
      tvSub.setTextColor(muted(a));
      tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
      tvSub.setPadding(0, a.dp(1), 0, 0);
      col.addView(tvSub);
    }

    row.addView(col, new LinearLayout.LayoutParams(0, -2, 1f));

    TextView arrow = new TextView(a);
    arrow.setText("›");
    arrow.setTextColor(accent(a));
    arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
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

  private static TextView chipBtn(MainActivity a, String label, boolean primary, Runnable onClick) {
    TextView tv = new TextView(a);
    tv.setText(label);
    tv.setTextColor(primary ? 0xFF0A1422 : INK);
    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
    tv.setGravity(Gravity.CENTER);
    tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    tv.setPadding(a.dp(8), a.dp(7), a.dp(8), a.dp(7));
    tv.setBackground(roundBg(a, primary ? accent(a) : bgCard(a), primary ? 0 : STROKE_SUBTLE, 10));
    tv.setOnClickListener(v -> safeRun(a, onClick));
    return tv;
  }

  private static View buildStatPill(MainActivity a, String val, String label) {
    LinearLayout box = new LinearLayout(a);
    box.setOrientation(LinearLayout.VERTICAL);
    box.setGravity(Gravity.CENTER);
    box.setPadding(a.dp(6), a.dp(7), a.dp(6), a.dp(7));
    box.setBackground(roundBg(a, bgCard(a), STROKE_SUBTLE, 12));

    TextView v = new TextView(a);
    v.setText(val);
    v.setTextColor(INK);
    v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
    v.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    box.addView(v);

    TextView l = new TextView(a);
    l.setText(label);
    l.setTextColor(muted(a));
    l.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
    box.addView(l);
    return box;
  }

  private static View buildDailyWordCard(MainActivity a, Runnable dismissHub) {
    int dayIdx = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % DAILY_WORDS.length;
    final String[] w = DAILY_WORDS[dayIdx];
    final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    final boolean done = a.prefs != null && today.equals(a.prefs.getString(DAILY_DONE_KEY, ""));

    LinearLayout card = new LinearLayout(a);
    card.setOrientation(LinearLayout.VERTICAL);
    card.setPadding(a.dp(12), a.dp(11), a.dp(12), a.dp(11));
    card.setBackground(roundBg(a, bgBadge(a), accent(a), 16));

    TextView tag = new TextView(a);
    tag.setText(done ? "PALABRA DEL DÍA · COMPLETADA" : "PALABRA DEL DÍA · RETO +20 XP");
    tag.setTextColor(accent(a));
    tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
    tag.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    tag.setLetterSpacing(0.06f);
    card.addView(tag);

    TextView word = new TextView(a);
    word.setText(w[0] + " — " + w[1]);
    word.setTextColor(INK);
    word.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.5f);
    word.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
    word.setPadding(0, a.dp(3), 0, a.dp(2));
    card.addView(word);

    TextView ex = new TextView(a);
    ex.setText("«" + w[2] + "»");
    ex.setTextColor(muted(a));
    ex.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
    ex.setPadding(0, 0, 0, a.dp(9));
    card.addView(ex);

    LinearLayout btnRow = new LinearLayout(a);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);

    TextView bHear = chipBtn(a, "▶ Oír italiano", false, () -> a.speak(w[0] + ". " + w[2], 0, 0.92f));
    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, -2, 1f);
    lp1.setMarginEnd(a.dp(6));
    btnRow.addView(bHear, lp1);

    TextView bDone = chipBtn(a, done ? "Practicar" : "Completar +20 XP", true, () -> {
      if (!done && a.prefs != null) {
        a.prefs.edit().putString(DAILY_DONE_KEY, today).apply();
        recordActivity(a, 20);
        a.toast("¡Reto completado! +20 XP");
      }
      dismissHub.run();
      a.ask("Ciao Sofia! Parliamo solo in italiano: fammi praticare la parola '" + w[0] + "' con un esempio semplice.");
    });
    btnRow.addView(bDone, new LinearLayout.LayoutParams(0, -2, 1f));

    card.addView(btnRow);
    return card;
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

    LinearLayout outer = new LinearLayout(a);
    outer.setOrientation(LinearLayout.VERTICAL);

    // Cabecera limpia
    LinearLayout head = dialogHeader(a, "IMPARA L'ITALIANO · ESTUDIO", "Centro de aprendizaje");
    outer.addView(head);

    // 3 metricas compactas en horizontal
    LinearLayout stats = new LinearLayout(a);
    stats.setOrientation(LinearLayout.HORIZONTAL);
    LinearLayout.LayoutParams sLp1 = new LinearLayout.LayoutParams(0, -2, 1f); sLp1.setMarginEnd(a.dp(5));
    LinearLayout.LayoutParams sLp2 = new LinearLayout.LayoutParams(0, -2, 1f); sLp2.setMarginEnd(a.dp(5));
    LinearLayout.LayoutParams sLp3 = new LinearLayout.LayoutParams(0, -2, 1f);
    stats.addView(buildStatPill(a, streak + "d", "Racha"), sLp1);
    stats.addView(buildStatPill(a, xp + " XP", "Puntos"), sLp2);
    stats.addView(buildStatPill(a, level, "Nivel"), sLp3);
    outer.addView(stats, rowGap(a, 10));

    // Segmented Control: 3 Pestanas (Aprender | Practicar | Ajustes)
    LinearLayout tabsBar = new LinearLayout(a);
    tabsBar.setOrientation(LinearLayout.HORIZONTAL);
    tabsBar.setPadding(a.dp(3), a.dp(3), a.dp(3), a.dp(3));
    tabsBar.setBackground(roundBg(a, bgCard(a), STROKE_SUBTLE, 14));

    final String[] tabTitles = {"Aprender", "Practicar", "Ajustes"};
    final TextView[] tabBtns = new TextView[3];
    final ScrollView bodyScroll = new ScrollView(a);
    bodyScroll.setVerticalScrollBarEnabled(false);
    final LinearLayout bodyContainer = new LinearLayout(a);
    bodyContainer.setOrientation(LinearLayout.VERTICAL);
    bodyScroll.addView(bodyContainer);

    final AlertDialog[] holder = new AlertDialog[1];
    Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

    Runnable[] renderTab = new Runnable[3];

    // TAB 0: APRENDER (Palabra del Dia + Cuadricula 2x2)
    renderTab[0] = () -> {
      bodyContainer.removeAllViews();
      bodyContainer.addView(buildDailyWordCard(a, dismiss), rowGap(a, 10));

      View tVerbs = buildGridTile(a, "V", "50 Verbos", "Conjugador offline y audio", () -> {
        dismiss.run(); openVerbConjugator(a);
      });
      View tCards = buildGridTile(a, "♡", "Flashcards", "Tu mazo de repaso diario", () -> {
        dismiss.run(); openFlashcards(a, selectedText);
      });
      bodyContainer.addView(buildTwoColRow(a, tVerbs, tCards), rowGap(a, 8));

      View tFalse = buildGridTile(a, "!", "Falsos Amigos", "Trampas ES/IT y quiz", () -> {
        dismiss.run(); openFalseFriends(a);
      });
      View tQuick = buildGridTile(a, "▶", "Frases Rápidas", "Audio natural en 1 toque", () -> {
        dismiss.run(); openQuickPhrases(a);
      });
      bodyContainer.addView(buildTwoColRow(a, tFalse, tQuick), rowGap(a, 4));
    };

    // TAB 1: PRACTICAR (Cuadricula 2x2 + extras)
    renderTab[1] = () -> {
      bodyContainer.removeAllViews();
      View tShad = buildGridTile(a, "◎", "Shadowing", "Normal 1.0x y lento 0.75x", () -> {
        dismiss.run(); openShadowing(a, selectedText);
      });
      View tRole = buildGridTile(a, "❖", "Escenarios", "Bar, hotel, charla y nota", () -> {
        dismiss.run(); openRoleplay(a);
      });
      bodyContainer.addView(buildTwoColRow(a, tShad, tRole), rowGap(a, 8));

      View tCorr = buildGridTile(a, "✦", "Corrector IA", "Mejora tu frase con Sofía", () -> {
        dismiss.run(); correctPhrase(a, selectedText);
      });
      View tGame = buildGridTile(a, "?", "Mini-Juego", "Reto rápido de agilidad", () -> {
        dismiss.run(); openMiniGame(a, 0, 0);
      });
      bodyContainer.addView(buildTwoColRow(a, tCorr, tGame), rowGap(a, 8));

      View tStory = buildGridTile(a, "≡", "Historias", "Cuento + 3 preguntas", () -> {
        dismiss.run(); openStoryMode(a);
      });
      View tSlang = buildGridTile(a, "»", "Jerga Real", "Modismos e intérprete", () -> {
        dismiss.run(); openSlang(a);
      });
      bodyContainer.addView(buildTwoColRow(a, tStory, tSlang), rowGap(a, 4));
    };

    // TAB 2: AJUSTES (Nivel rapido + Temas + Estilo + Historial + Respaldo)
    renderTab[2] = () -> {
      bodyContainer.removeAllViews();

      // Selector rapido de nivel (4 botones en linea)
      TextView lblLvl = new TextView(a);
      lblLvl.setText("NIVEL DE SOFÍA");
      lblLvl.setTextColor(muted(a));
      lblLvl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
      lblLvl.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
      lblLvl.setPadding(0, 0, 0, a.dp(6));
      bodyContainer.addView(lblLvl);

      LinearLayout lvlRow = new LinearLayout(a);
      lvlRow.setOrientation(LinearLayout.HORIZONTAL);
      final String[] lvls = {"A1", "A2", "B1", "B2"};
      final String[] prompts = {
        "Ciao Sofia! Parliamo solo in italiano a livello A1 con frasi molto brevi.",
        "Ciao Sofia! Parliamo solo in italiano a livello A2 con frasi quotidiane.",
        "Ciao Sofia! Parliamo solo in italiano a livello B1 in modo naturale.",
        "Ciao Sofia! Parliamo solo in italiano a livello B2 con ritmo naturale."
      };
      String curLvl = a.prefs != null ? a.prefs.getString(LEVEL_KEY, "A2") : "A2";
      for (int i = 0; i < lvls.length; i++) {
        final int idx = i;
        boolean active = lvls[i].equals(curLvl);
        TextView btn = chipBtn(a, lvls[i], active, () -> {
          a.prefs.edit().putString(LEVEL_KEY, lvls[idx]).apply();
          a.toast("Nivel: " + lvls[idx]);
          dismiss.run();
          a.ask(prompts[idx]);
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
        if (i < lvls.length - 1) lp.setMarginEnd(a.dp(6));
        lvlRow.addView(btn, lp);
      }
      bodyContainer.addView(lvlRow, rowGap(a, 12));

      // Selector rapido de tema (4 botones en linea)
      TextView lblTheme = new TextView(a);
      lblTheme.setText("TEMA VISUAL");
      lblTheme.setTextColor(muted(a));
      lblTheme.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
      lblTheme.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
      lblTheme.setPadding(0, 0, 0, a.dp(6));
      bodyContainer.addView(lblTheme);

      LinearLayout thRow = new LinearLayout(a);
      thRow.setOrientation(LinearLayout.HORIZONTAL);
      int curTh = a.prefs != null ? a.prefs.getInt(THEME_KEY, 0) : 0;
      for (int i = 0; i < THEME_NAMES.length; i++) {
        final int idx = i;
        TextView btn = chipBtn(a, THEME_NAMES[i], i == curTh, () -> {
          a.prefs.edit().putInt(THEME_KEY, idx).apply();
          if (a.chat != null) a.chat.setBackgroundColor(THEMES[idx][0]);
          a.toast("Tema: " + THEME_NAMES[idx]);
          dismiss.run();
          openHub(a, selectedText);
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
        if (i < THEME_NAMES.length - 1) lp.setMarginEnd(a.dp(5));
        thRow.addView(btn, lp);
      }
      bodyContainer.addView(thRow, rowGap(a, 12));

      String style = a.prefs != null ? a.prefs.getString(STYLE_KEY, "Profesora cercana") : "Profesora cercana";
      bodyContainer.addView(buildOptionRow(a, "★", "Estilo de Sofía", "Actual: " + style, () -> {
        dismiss.run(); chooseSofiaStyle(a);
      }), rowGap(a, 7));

      bodyContainer.addView(buildOptionRow(a, "⇄", "Intérprete ES ↔ IT", "Traduce y escucha al instante", () -> {
        dismiss.run(); openInterpreter(a, selectedText);
      }), rowGap(a, 7));

      bodyContainer.addView(buildOptionRow(a, "≡", "Historial de chats", "Guardar, abrir o borrar sesiones", () -> {
        dismiss.run(); showHistoryDialog(a);
      }), rowGap(a, 7));

      bodyContainer.addView(buildOptionRow(a, "⇪", "Respaldo JSON", "Exportar o importar tu progreso", () -> {
        dismiss.run(); openBackupDialog(a);
      }), rowGap(a, 0));
    };

    final Runnable selectTab[] = new Runnable[3];
    for (int i = 0; i < 3; i++) {
      final int idx = i;
      TextView tb = new TextView(a);
      tb.setText(tabTitles[i]);
      tb.setGravity(Gravity.CENTER);
      tb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
      tb.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
      tb.setPadding(a.dp(6), a.dp(8), a.dp(6), a.dp(8));
      tabBtns[i] = tb;
      selectTab[i] = () -> {
        for (int j = 0; j < 3; j++) {
          boolean on = (j == idx);
          tabBtns[j].setTextColor(on ? 0xFF0A1422 : muted(a));
          tabBtns[j].setBackground(on ? roundBg(a, accent(a), 0, 11) : null);
        }
        renderTab[idx].run();
      };
      tb.setOnClickListener(v -> selectTab[idx].run());
      tabsBar.addView(tb, new LinearLayout.LayoutParams(0, -2, 1f));
    }

    outer.addView(tabsBar, rowGap(a, 10));
    outer.addView(bodyScroll, new LinearLayout.LayoutParams(-1, a.dp(340)));

    selectTab[0].run();

    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    styleDialogWindow(a, dialog, outer);
  }

  public static void showHistoryDialog(MainActivity a) {
    try {
      JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
      ScrollView scroll = new ScrollView(a);
      LinearLayout root = dialogHeader(a, "HISTORIAL OFFLINE", "Conversaciones guardadas");

      final AlertDialog[] holder = new AlertDialog[1];
      Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

      root.addView(buildOptionRow(a, "+", "Guardar conversación actual",
        "Guarda todos los mensajes visibles", () -> {
          dismiss.run();
          ChatBubbles.saveCurrentChat(a);
        }), rowGap(a, 7));

      for (int i = arr.length() - 1; i >= 0; i--) {
        final int idx = i;
        JSONObject o = arr.getJSONObject(i);
        int count = o.optJSONArray("msgs") != null ? o.optJSONArray("msgs").length() : 0;
        root.addView(buildOptionRow(a, "≡", o.optString("title", "Sesión"),
          count + " mensajes", () -> {
            dismiss.run();
            ChatBubbles.openSavedChat(a, idx);
          }), rowGap(a, 7));
      }

      scroll.addView(root);
      AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
      holder[0] = dialog;
      styleDialogWindow(a, dialog, scroll);
    } catch (Exception e) {
      a.toast("Error abriendo historial");
    }
  }

  private static void openVerbConjugator(MainActivity a) {
    LinearLayout root = dialogHeader(a, "50 VERBOS ESENCIALES · OFFLINE", "Conjugador rápido");
    EditText search = styledInput(a, "Buscar verbo (ej. essere, ir, comer)...", "");
    root.addView(search, rowGap(a, 8));

    ScrollView scroll = new ScrollView(a);
    scroll.setVerticalScrollBarEnabled(false);
    LinearLayout list = new LinearLayout(a);
    list.setOrientation(LinearLayout.VERTICAL);
    scroll.addView(list);
    root.addView(scroll, new LinearLayout.LayoutParams(-1, a.dp(340)));

    Runnable render = () -> {
      list.removeAllViews();
      String q = search.getText().toString().trim().toLowerCase(Locale.ROOT);
      for (String[] v : VERBS_50) {
        if (q.isEmpty() || v[0].contains(q) || v[1].toLowerCase(Locale.ROOT).contains(q)) {
          final String[] verb = v;
          list.addView(buildOptionRow(a, "V", verb[0] + " (" + verb[1] + ")",
            "Pres: " + verb[2], () -> showVerbDetail(a, verb)), rowGap(a, 6));
        }
      }
    };
    render.run();

    search.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      public void onTextChanged(CharSequence s, int start, int before, int count) { render.run(); }
      public void afterTextChanged(Editable s) {}
    });

    styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), root);
  }

  private static void showVerbDetail(MainActivity a, String[] v) {
    recordActivity(a, 5);
    ScrollView scroll = new ScrollView(a);
    LinearLayout box = dialogHeader(a, "CONJUGACIÓN · TOCA PARA OÍR", v[0] + " (" + v[1] + ")");
    box.addView(buildOptionRow(a, "▶", "Presente", v[2], () -> a.speak(v[2], 0, 0.9f)), rowGap(a, 7));
    box.addView(buildOptionRow(a, "▶", "Passato Prossimo", "io " + v[3], () -> a.speak("io " + v[3], 0, 0.9f)), rowGap(a, 7));
    box.addView(buildOptionRow(a, "▶", "Imperfetto", v[4], () -> a.speak(v[4], 0, 0.9f)), rowGap(a, 7));
    box.addView(buildOptionRow(a, "▶", "Futuro Semplice", v[5], () -> a.speak(v[5], 0, 0.9f)), rowGap(a, 0));
    scroll.addView(box);

    AlertDialog d = new AlertDialog.Builder(a)
      .setPositiveButton("Practicar con Sofía", (x, y) -> a.ask("Ciao Sofia! Parliamo solo in italiano: fammi 3 frasi con il verbo '" + v[0] + "'."))
      .setNegativeButton("Cerrar", null)
      .create();
    styleDialogWindow(a, d, scroll);
  }

  private static void openFalseFriends(MainActivity a) {
    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "TRAMPAS ES vs IT · OFFLINE", "Falsos Amigos");
    final AlertDialog[] holder = new AlertDialog[1];

    root.addView(buildOptionRow(a, "?", "Iniciar Mini-Quiz de Falsos Amigos",
      "Pon a prueba tu reflejo (+10 XP por acierto)", () -> {
        if (holder[0] != null) holder[0].dismiss();
        runFalseFriendQuiz(a, 0, 0);
      }), rowGap(a, 7));

    for (String[] f : FALSE_FRIENDS) {
      final String[] ff = f;
      root.addView(buildOptionRow(a, "!", ff[0] + " = " + ff[1],
        ff[2] + "\nEj: " + ff[3], () -> {
          recordActivity(a, 3);
          a.speak(ff[0] + ". " + ff[3], 0, 0.9f);
        }), rowGap(a, 7));
    }
    scroll.addView(root);
    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    styleDialogWindow(a, dialog, scroll);
  }

  private static void runFalseFriendQuiz(MainActivity a, int idx, int score) {
    int total = 5;
    if (idx >= total) {
      recordActivity(a, score * 10);
      LinearLayout box = dialogHeader(a, "QUIZ TERMINADO", "Aciertos: " + score + " / " + total);
      TextView tv = new TextView(a);
      tv.setText("Ganaste +" + (score * 10) + " XP.");
      tv.setTextColor(muted(a));
      box.addView(tv);
      styleDialogWindow(a, new AlertDialog.Builder(a).setPositiveButton("¡Genial!", null).create(), box);
      return;
    }
    Random r = new Random();
    int target = (idx * 2 + r.nextInt(2)) % FALSE_FRIENDS.length;
    final String[] item = FALSE_FRIENDS[target];
    ArrayList<String> opts = new ArrayList<>();
    opts.add(item[1]);
    while (opts.size() < 3) {
      String cand = FALSE_FRIENDS[r.nextInt(FALSE_FRIENDS.length)][1];
      if (!opts.contains(cand)) opts.add(cand);
    }
    Collections.shuffle(opts);

    LinearLayout root = dialogHeader(a, "FALSOS AMIGOS · " + (idx + 1) + "/" + total,
      "¿Qué significa «" + item[0] + "» en italiano?");
    final AlertDialog[] holder = new AlertDialog[1];

    for (String opt : opts) {
      final String choice = opt;
      root.addView(buildOptionRow(a, "»", choice, "Toca para responder", () -> {
        if (holder[0] != null) holder[0].dismiss();
        boolean ok = choice.equals(item[1]);
        a.toast(ok ? "¡Correcto! " + item[0] + " = " + item[1] : "Era: " + item[1]);
        runFalseFriendQuiz(a, idx + 1, score + (ok ? 1 : 0));
      }), rowGap(a, 7));
    }
    AlertDialog d = new AlertDialog.Builder(a).setNegativeButton("Salir", null).create();
    holder[0] = d;
    styleDialogWindow(a, d, root);
  }

  private static void openShadowing(MainActivity a, String initialPhrase) {
    String cleanInitial = extractItalianOnly(initialPhrase);
    final String[] presets = {
      !TextUtils.isEmpty(cleanInitial) ? cleanInitial : "Vorrei un caffè macchiato e un cornetto, per favore.",
      "Scusa, puoi parlare un po' più lentamente?",
      "Mi piace molto imparare l'italiano ogni giorno.",
      "Che bella giornata per fare una passeggiata in centro!",
      "Non vedo l'ora di visitare Roma e Firenze."
    };

    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "ENTRENADOR DE ACENTO", "Modo Shadowing");
    TextView info = new TextView(a);
    info.setText("Sofía dice la frase en italiano normal (1.0x), hace una pausa para que repitas y luego la repite lenta (0.75x).");
    info.setTextColor(muted(a));
    info.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
    info.setPadding(0, 0, 0, a.dp(8));
    root.addView(info);

    for (int i = 0; i < presets.length; i++) {
      final String phrase = presets[i];
      String label = (i == 0 && !TextUtils.isEmpty(cleanInitial)) ? "Frase del mensaje actual" : "Práctica #" + (i + 1);
      root.addView(buildOptionRow(a, "◎", label, phrase, () -> startShadowingCycle(a, phrase)), rowGap(a, 7));
    }

    scroll.addView(root);
    styleDialogWindow(a, new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create(), scroll);
  }

  private static void openBackupDialog(MainActivity a) {
    ScrollView scroll = new ScrollView(a);
    LinearLayout root = dialogHeader(a, "SEGURIDAD OFFLINE", "Respaldo de progreso (JSON)");
    final AlertDialog[] holder = new AlertDialog[1];

    root.addView(buildOptionRow(a, "⇪", "Exportar todo al portapapeles",
      "Copia flashcards, XP, racha, nivel, tema e historial", () -> {
        try {
          JSONObject out = new JSONObject();
          out.put("v", 1);
          out.put("cards", a.prefs.getString(CARDS_KEY, "[]"));
          out.put("history", a.prefs.getString(HIST_KEY, "[]"));
          out.put("xp", a.prefs.getInt(XP_KEY, 0));
          out.put("streak", a.prefs.getInt(STREAK_KEY, 1));
          out.put("last_day", a.prefs.getString(LAST_DAY_KEY, ""));
          out.put("level", a.prefs.getString(LEVEL_KEY, "A2"));
          out.put("theme", a.prefs.getInt(THEME_KEY, 0));
          out.put("style", a.prefs.getString(STYLE_KEY, "Profesora cercana"));
          ClipboardManager cm = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
          if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("ImparaBackup", out.toString()));
            a.toast("Respaldo copiado al portapapeles");
          }
        } catch (Exception e) {
          a.toast("Error exportando respaldo");
        }
      }), rowGap(a, 7));

    root.addView(buildOptionRow(a, "⇩", "Importar respaldo JSON",
      "Pega aquí tu código guardado para restaurar todo", () -> {
        if (holder[0] != null) holder[0].dismiss();
        LinearLayout box = dialogHeader(a, "RESTAURAR DATOS", "Pega tu respaldo JSON");
        EditText et = styledInput(a, "{\"v\":1,...}", "");
        box.addView(et);
        AlertDialog d2 = new AlertDialog.Builder(a)
          .setPositiveButton("Restaurar", (x, y) -> {
            try {
              JSONObject in = new JSONObject(et.getText().toString().trim());
              a.prefs.edit()
                .putString(CARDS_KEY, in.optString("cards", "[]"))
                .putString(HIST_KEY, in.optString("history", "[]"))
                .putInt(XP_KEY, in.optInt("xp", 0))
                .putInt(STREAK_KEY, in.optInt("streak", 1))
                .putString(LAST_DAY_KEY, in.optString("last_day", ""))
                .putString(LEVEL_KEY, in.optString("level", "A2"))
                .putInt(THEME_KEY, in.optInt("theme", 0))
                .putString(STYLE_KEY, in.optString("style", "Profesora cercana"))
                .apply();
              a.toast("Progreso restaurado con éxito");
            } catch (Exception e) {
              a.toast("JSON inválido");
            }
          })
          .setNegativeButton("Cancelar", null)
          .create();
        styleDialogWindow(a, d2, box);
      }), rowGap(a, 0));

    scroll.addView(root);
    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    styleDialogWindow(a, dialog, scroll);
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
          a.ask("Rispondi solo in italiano: traduci in italiano naturale questa frase:\n" + txt);
        }
      })
      .setNeutralButton("▶ Leer IT", (d, w) -> {
        String txt = input.getText().toString().trim();
        if (!txt.isEmpty()) speakItalianOnly(a, txt, 0.95f);
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

    for (int i = 0; i < terms.length; i++) {
      final int idx = i;
      root.addView(buildOptionRow(a, "▶", terms[i], defs[i], () -> {
        recordActivity(a, 5);
        a.speak(terms[idx], 0, 0.9f);
      }), rowGap(a, 7));
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
      tv.setText("Ganaste +" + (score * 10) + " XP.");
      tv.setTextColor(muted(a));
      box.addView(tv);
      styleDialogWindow(a, new AlertDialog.Builder(a).setPositiveButton("¡Genial!", null).create(), box);
      return;
    }

    final String[] q = qs[qIdx];
    ArrayList<String> opts = new ArrayList<>(Arrays.asList(q[1], q[2], q[3]));
    Collections.shuffle(opts);

    LinearLayout root = dialogHeader(a, "RETO " + (qIdx + 1) + "/" + qs.length, q[0]);
    final AlertDialog[] holder = new AlertDialog[1];

    for (String opt : opts) {
      final String choice = opt;
      root.addView(buildOptionRow(a, "»", choice, "Toca para responder", () -> {
        if (holder[0] != null) holder[0].dismiss();
        boolean ok = choice.equals(q[1]);
        a.toast(ok ? "¡Correcto!" : "Era: " + q[1]);
        openMiniGame(a, qIdx + 1, score + (ok ? 1 : 0));
      }), rowGap(a, 7));
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

    for (String g : genres) {
      final String genre = g;
      root.addView(buildOptionRow(a, "≡", genre, "Cuento corto + 3 preguntas", () -> {
        if (holder[0] != null) holder[0].dismiss();
        recordActivity(a, 15);
        a.ask("Rispondi solo in italiano: raccontami una storia breve sul tema '" + genre + "' e alla fine fammi 3 domande.");
      }), rowGap(a, 7));
    }
    scroll.addView(root);
    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    styleDialogWindow(a, dialog, scroll);
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
      Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

      root.addView(buildOptionRow(a, "+", "Guardar frase actual", "Añadir a tus tarjetas de repaso", () -> {
        dismiss.run();
        saveFlashcardDirect(a, !TextUtils.isEmpty(currentPhrase) ? currentPhrase : "Ciao! Come stai oggi?");
      }), rowGap(a, 7));

      for (int i = cards.length() - 1; i >= 0; i--) {
        final int idx = i;
        final String phrase = cards.getJSONObject(i).optString("it", "");
        root.addView(buildOptionRow(a, "♡", phrase, "Toca para escuchar, shadowing o eliminar", () -> {
          dismiss.run();
          LinearLayout box = dialogHeader(a, "REPASO", "Tarjeta guardada");
          TextView tv = new TextView(a);
          tv.setText(phrase);
          tv.setTextColor(INK);
          tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
          box.addView(tv);
          AlertDialog d2 = new AlertDialog.Builder(a)
            .setPositiveButton("▶ Normal", (x, y) -> speakItalianOnly(a, phrase, 1.0f))
            .setNeutralButton("◎ Shadowing", (x, y) -> startShadowingCycle(a, phrase))
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
