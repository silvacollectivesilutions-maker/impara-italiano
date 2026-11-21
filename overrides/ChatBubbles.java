package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

/** Burbujas minimalistas optimizadas: sin trabas al cargar chats, borrar directo y audio 100% italiano. */
public final class ChatBubbles {
  private static final int INK = 0xffffffff;
  private static final String HIST_KEY = "impara_chat_history_v1";
  private static final Handler UI = new Handler(Looper.getMainLooper());
  private static final Typeface TF_MED = Typeface.create("sans-serif-medium", Typeface.NORMAL);
  private static final ArrayList<Runnable> PENDING_FIXES = new ArrayList<>();
  private static Runnable pendingPolish;

  static void detach(View v) {
    if (v != null && v.getParent() instanceof ViewGroup) {
      ((ViewGroup) v.getParent()).removeView(v);
    }
  }

  static ArrayList<View> children(ViewGroup g) {
    ArrayList<View> r = new ArrayList<>();
    for (int i = 0; i < g.getChildCount(); i++) {
      r.add(g.getChildAt(i));
    }
    return r;
  }

  static void buttons(View v, View table, List<Button> out) {
    if (v == table) return;
    if (v instanceof Button) {
      out.add((Button) v);
    } else if (v instanceof ViewGroup) {
      for (View c : children((ViewGroup) v)) {
        buttons(c, table, out);
      }
    }
  }

  private static void styleActionIcon(MainActivity a, Button b, String glyph, String desc, boolean danger) {
    try {
      if (b == null) return;
      b.setAllCaps(false);
      b.setSingleLine(true);
      b.setMaxLines(1);
      b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
      b.setTextColor(danger ? 0xFFFF8A8A : 0xFFE6F0FA);
      b.setTypeface(TF_MED);
      b.setMinWidth(0);
      b.setMinimumWidth(0);
      b.setMinHeight(0);
      b.setMinimumHeight(0);
      b.setPadding(0, 0, 0, 0);
      b.setGravity(Gravity.CENTER);
      b.setCompoundDrawables(null, null, null, null);
      b.setCompoundDrawablesRelative(null, null, null, null);
      b.setCompoundDrawablePadding(0);
      if (!TextUtils.isEmpty(glyph)) b.setText(glyph);
      if (!TextUtils.isEmpty(desc)) b.setContentDescription(desc);

      GradientDrawable bg = new GradientDrawable();
      bg.setShape(GradientDrawable.RECTANGLE);
      bg.setColor(danger ? 0x22FF5C5C : 0x1AFFFFFF);
      bg.setCornerRadius(a.dp(999));
      bg.setStroke(Math.max(1, a.dp(1)), danger ? 0x38FF7070 : 0x22FFFFFF);
      b.setBackground(bg);

      int w = a.dp(30);
      int h = a.dp(28);
      ViewGroup.LayoutParams lp = b.getLayoutParams();
      if (lp != null && (lp.width != w || lp.height != h)) {
        lp.width = w;
        lp.height = h;
        b.setLayoutParams(lp);
      }
    } catch (Throwable ignored) {}
  }

  private static void schedulePolish(MainActivity a, Runnable fixButtons) {
    synchronized (PENDING_FIXES) {
      PENDING_FIXES.add(fixButtons);
    }
    if (pendingPolish != null) UI.removeCallbacks(pendingPolish);
    pendingPolish = () -> {
      try {
        ButtonStudio.apply(a);
        ArrayList<Runnable> batch;
        synchronized (PENDING_FIXES) {
          batch = new ArrayList<>(PENDING_FIXES);
          PENDING_FIXES.clear();
        }
        for (Runnable r : batch) {
          r.run();
        }
        ImparaSuperApp.attachStudioFab(a);
      } catch (Throwable ignored) {}
    };
    UI.postDelayed(pendingPolish, 120L);
  }

  public static TextView create(MainActivity a, String value, boolean user) {
    if (user) {
      try {
        MoreCards546.user(a, value);
        ImparaSuperApp.recordActivity(a, 5);
      } catch (Throwable ignored) {}
    }

    TextView text = a.legacyMessage(value, user);
    if (a.page != 1 || a.chat == null) return text;

    try {
      View old = text;
      while (old.getParent() instanceof View && old.getParent() != a.chat) {
        old = (View) old.getParent();
      }
      if (old.getParent() != a.chat || !(old instanceof ViewGroup)) return text;

      int index = a.chat.indexOfChild(old);
      View table = old.findViewWithTag("study-columns");
      ArrayList<Button> all = new ArrayList<>();
      buttons(old, table, all);

      Button audio = null, translate = null, explain = null, columns = null, spanishAudio = null;
      for (Button b : all) {
        String d = String.valueOf(b.getContentDescription());
        String label = b.getText().toString();
        if (d.contains("columnas")) columns = b;
        else if (d.contains("traducción")) translate = b;
        else if (d.contains("español") && d.contains("Escuchar")) spanishAudio = b;
        else if (b.getTag() == text || d.contains("Paola") || d.contains("Escuchar o detener")) audio = b;
        else if (label.contains("Explicar") || label.contains("significado") || label.contains("Rayos X")) explain = b;
      }

      ArrayList<TextView> translations = new ArrayList<>();
      for (View v : children((ViewGroup) old)) {
        if (v instanceof TextView && !(v instanceof Button) && v != text) {
          translations.add((TextView) v);
        }
      }

      LinearLayout bubble = new LinearLayout(a);
      bubble.setOrientation(LinearLayout.VERTICAL);
      bubble.setPadding(a.dp(12), a.dp(10), a.dp(12), a.dp(6));
      bubble.setBackground(new WallStyle.Bubble(a, user));
      bubble.setTag(0x7e002602, user);
      final Button fColumnsRef = columns;
      bubble.setOnLongClickListener(v -> {
        showCompactBubbleMenu(a, bubble, text, user, fColumnsRef);
        return true;
      });

      LinearLayout body = new LinearLayout(a);
      body.setOrientation(LinearLayout.VERTICAL);
      detach(text);
      text.setTag(0x7e002601, true);
      text.setTextColor(a.prefs.getInt("chat_text_color", INK));
      text.setTextSize(TypedValue.COMPLEX_UNIT_SP, a.prefs.getInt("chat_text_size", 16));
      text.setIncludeFontPadding(false);
      text.setPadding(0, 0, 0, 0);
      text.setLineSpacing(a.dp(3), 1f);
      body.addView(text, new LinearLayout.LayoutParams(-1, -2));
      bubble.addView(body, new LinearLayout.LayoutParams(-1, -2));

      for (TextView t : translations) {
        detach(t);
        t.setTag(0x7e002601, true);
        t.setTextColor(a.prefs.getInt("chat_text_color", INK));
        t.setPadding(a.dp(8), a.dp(6), a.dp(8), a.dp(6));
        SofiaGlass.surface(a, t, 10, false, 0, false);
        bubble.addView(t, new LinearLayout.LayoutParams(-1, -2));
      }

      if (spanishAudio != null) detach(spanishAudio);
      if (audio != null) detach(audio);

      if (table != null) {
        detach(table);
        bubble.addView(table, new LinearLayout.LayoutParams(-1, -2));
      }

      LinearLayout actions = new LinearLayout(a);
      actions.setOrientation(LinearLayout.HORIZONTAL);
      actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
      actions.setPadding(0, a.dp(6), 0, a.dp(1));

      Button playBtn = DeskUI.button(a, "▶", () -> ImparaSuperApp.speakItalianOnly(a, text.getText().toString(), 1.0f), false);
      playBtn.setOnLongClickListener(v -> {
        ImparaSuperApp.speakItalianOnly(a, text.getText().toString(), 0.75f);
        a.toast("Lento 0.75x (italiano)");
        return true;
      });
      addIcon(a, actions, playBtn, "▶", "Oír italiano", false);

      Button editBtn = null;
      Button saveBtn = null;

      if (user) {
        editBtn = DeskUI.button(a, "✎", () -> editMessage(a, text, true), false);
        addIcon(a, actions, editBtn, "✎", "Editar", false);
      } else {
        if (translate != null) addIcon(a, actions, translate, "⇄", "Traducir", false);
        if (explain != null) addIcon(a, actions, explain, "✦", "Explicar", false);
        saveBtn = DeskUI.button(a, "♡", () -> ImparaSuperApp.saveFlashcardDirect(a, text.getText().toString()), false);
        addIcon(a, actions, saveBtn, "♡", "Guardar", false);
      }

      Button delBtn = DeskUI.button(a, "✕", () -> {
        if (bubble.getParent() instanceof ViewGroup) {
          ((ViewGroup) bubble.getParent()).removeView(bubble);
        }
        a.toast("Mensaje borrado");
      }, false);
      addIcon(a, actions, delBtn, "✕", "Borrar", true);

      Button moreBtn = DeskUI.button(a, "⋯", () -> showCompactBubbleMenu(a, bubble, text, user, fColumnsRef), false);
      addIcon(a, actions, moreBtn, "⋯", "Más", false);

      HorizontalScrollView strip = new HorizontalScrollView(a);
      strip.setHorizontalScrollBarEnabled(false);
      strip.setFillViewport(false);
      strip.setClipToPadding(false);
      strip.addView(actions, new ViewGroup.LayoutParams(-2, -2));
      bubble.addView(strip, new LinearLayout.LayoutParams(-1, -2));

      a.chat.removeView(old);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
      lp.setMargins(user ? a.dp(28) : 0, a.dp(4), user ? 0 : a.dp(16), a.dp(4));
      a.chat.addView(bubble, index, lp);

      final Button fPlay = playBtn;
      final Button fEdit = editBtn;
      final Button fTrans = translate;
      final Button fExp = explain;
      final Button fSave = saveBtn;
      final Button fDel = delBtn;
      final Button fMore = moreBtn;

      schedulePolish(a, () -> {
        styleActionIcon(a, fPlay, "▶", "Oír italiano", false);
        styleActionIcon(a, fEdit, "✎", "Editar", false);
        styleActionIcon(a, fTrans, "⇄", "Traducir", false);
        styleActionIcon(a, fExp, "✦", "Explicar", false);
        styleActionIcon(a, fSave, "♡", "Guardar", false);
        styleActionIcon(a, fDel, "✕", "Borrar", true);
        styleActionIcon(a, fMore, "⋯", "Más", false);
      });

      StudyDesk.selection(a, text);
    } catch (Throwable ignored) {}
    return text;
  }

  private static void addIcon(MainActivity a, LinearLayout row, Button b, String glyph, String desc, boolean danger) {
    if (b == null) return;
    detach(b);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(a.dp(30), a.dp(28));
    lp.setMarginStart(a.dp(5));
    row.addView(b, lp);
    styleActionIcon(a, b, glyph, desc, danger);
  }

  private static void showCompactBubbleMenu(MainActivity a, LinearLayout bubble, TextView text, boolean user, Button columnsBtn) {
    String msg = text.getText().toString();
    LinearLayout root = ImparaSuperApp.dialogHeader(a,
      user ? "TU MENSAJE" : "MENSAJE DE SOFÍA",
      "Acciones rápidas");

    final AlertDialog[] holder = new AlertDialog[1];
    Runnable dismiss = () -> { if (holder[0] != null && holder[0].isShowing()) holder[0].dismiss(); };

    LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-1, -2);
    gap.setMargins(0, 0, 0, a.dp(8));

    View tSlow = ImparaSuperApp.buildGridTile(a, "▶", "Lento 0.75x", "Solo italiano despacio", () -> {
      dismiss.run(); ImparaSuperApp.speakItalianOnly(a, msg, 0.75f);
    });
    View tShad = ImparaSuperApp.buildGridTile(a, "◎", "Shadowing", "Normal y lento", () -> {
      dismiss.run(); ImparaSuperApp.startShadowingCycle(a, msg);
    });
    root.addView(ImparaSuperApp.buildTwoColRow(a, tSlow, tShad), gap);

    View tCopy = ImparaSuperApp.buildGridTile(a, "⧉", "Copiar", "Al portapapeles", () -> {
      dismiss.run();
      ClipboardManager cm = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
      if (cm != null) {
        cm.setPrimaryClip(ClipData.newPlainText("Impara l'italiano", msg));
        a.toast("Texto copiado");
      }
    });
    View tEdit = ImparaSuperApp.buildGridTile(a, "✎", "Editar", user ? "Corregir o reenviar" : "Editar texto", () -> {
      dismiss.run(); editMessage(a, text, user);
    });
    root.addView(ImparaSuperApp.buildTwoColRow(a, tCopy, tEdit), gap);

    View tThirdLeft = (columnsBtn != null)
      ? ImparaSuperApp.buildGridTile(a, "⊞", "Tabla", "Ver por columnas", () -> {
          dismiss.run(); columnsBtn.performClick();
        })
      : ImparaSuperApp.buildGridTile(a, "↗", "Compartir", "Enviar frase", () -> {
          dismiss.run(); ShareHub549.reply(a, text);
        });

    View tColor = ImparaSuperApp.buildGridTile(a, "◐", "Color", "Estilo de burbuja", () -> {
      dismiss.run(); ChatLook542.palette(a, user);
    });
    root.addView(ImparaSuperApp.buildTwoColRow(a, tThirdLeft, tColor), new LinearLayout.LayoutParams(-1, -2));

    AlertDialog dialog = new AlertDialog.Builder(a).setNegativeButton("Cerrar", null).create();
    holder[0] = dialog;
    ImparaSuperApp.styleDialogWindow(a, dialog, root);
  }

  private static void editMessage(MainActivity a, TextView text, boolean user) {
    LinearLayout box = ImparaSuperApp.dialogHeader(a, "IMPARA L'ITALIANO", "Editar mensaje");
    EditText et = ImparaSuperApp.styledInput(a, "Escribe tu mensaje...", text.getText().toString());
    box.addView(et);

    AlertDialog.Builder b = new AlertDialog.Builder(a)
      .setPositiveButton("Guardar", (d, w) -> {
        String val = et.getText().toString().trim();
        if (!val.isEmpty()) {
          text.setText(val);
          a.toast("Mensaje editado");
        }
      })
      .setNegativeButton("Cancelar", null);

    if (user) {
      b.setNeutralButton("Guardar y reenviar", (d, w) -> {
        String val = et.getText().toString().trim();
        if (!val.isEmpty()) {
          text.setText(val);
          a.ask(val);
        }
      });
    }
    ImparaSuperApp.styleDialogWindow(a, b.create(), box);
  }

  public static void saveCurrentChat(MainActivity a) {
    if (a.chat == null || a.chat.getChildCount() == 0) {
      a.toast("No hay mensajes para guardar");
      return;
    }
    try {
      JSONArray msgs = new JSONArray();
      for (int i = 0; i < a.chat.getChildCount(); i++) {
        View v = a.chat.getChildAt(i);
        Object uTag = v.getTag(0x7e002602);
        if (uTag instanceof Boolean && v instanceof ViewGroup) {
          TextView tv = findMainText((ViewGroup) v);
          if (tv != null) {
            JSONObject m = new JSONObject();
            m.put("u", (Boolean) uTag);
            m.put("t", tv.getText().toString());
            msgs.put(m);
          }
        }
      }
      if (msgs.length() == 0) {
        a.toast("Chat vacío");
        return;
      }
      JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
      JSONObject session = new JSONObject();
      session.put("title", new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date()));
      session.put("msgs", msgs);
      arr.put(session);
      a.prefs.edit().putString(HIST_KEY, arr.toString()).apply();
      a.toast("Conversación guardada (" + msgs.length() + " mensajes)");
    } catch (Exception e) {
      a.toast("No se pudo guardar");
    }
  }

  public static void openSavedChat(MainActivity a, int idx) {
    try {
      JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
      JSONObject session = arr.getJSONObject(idx);
      JSONArray msgs = session.getJSONArray("msgs");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < msgs.length(); i++) {
        JSONObject m = msgs.getJSONObject(i);
        sb.append(m.optBoolean("u") ? "Tú: " : "Sofía: ").append(m.optString("t")).append("\n\n");
      }
      ScrollView scroll = new ScrollView(a);
      LinearLayout box = ImparaSuperApp.dialogHeader(a, "SESIÓN GUARDADA", session.optString("title"));
      TextView body = new TextView(a);
      body.setText(sb.toString().trim());
      body.setTextColor(Color.parseColor("#E6EEF8"));
      body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
      body.setLineSpacing(a.dp(3), 1f);
      box.addView(body);
      scroll.addView(box);

      AlertDialog dialog = new AlertDialog.Builder(a)
        .setPositiveButton("Cargar en chat", (d, w) -> {
          a.chat.removeAllViews();
          for (int i = 0; i < msgs.length(); i++) {
            JSONObject m = msgs.optJSONObject(i);
            if (m != null) create(a, m.optString("t"), m.optBoolean("u"));
          }
          a.toast("Conversación cargada");
        })
        .setNeutralButton("Eliminar", (d, w) -> {
          arr.remove(idx);
          a.prefs.edit().putString(HIST_KEY, arr.toString()).apply();
          a.toast("Sesión eliminada");
        })
        .setNegativeButton("Cerrar", null)
        .create();
      ImparaSuperApp.styleDialogWindow(a, dialog, scroll);
    } catch (Exception e) {
      a.toast("Error al cargar");
    }
  }

  private static TextView findMainText(ViewGroup g) {
    for (int i = 0; i < g.getChildCount(); i++) {
      View c = g.getChildAt(i);
      if (c instanceof TextView && Boolean.TRUE.equals(c.getTag(0x7e002601))) return (TextView) c;
      if (c instanceof ViewGroup) {
        TextView r = findMainText((ViewGroup) c);
        if (r != null) return r;
      }
    }
    return null;
  }

  static void add(MainActivity a, LinearLayout row, Button b, int width, String label, int icon) {
    detach(b);
    row.addView(b, new LinearLayout.LayoutParams(a.dp(width > 0 ? width : 30), a.dp(28)));
    styleActionIcon(a, b, label, label, false);
  }
}
