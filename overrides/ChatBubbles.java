package com.germansilva.italianomaestro;
import android.app.AlertDialog;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

/** Builds the final bubble explicitly, retaining original callbacks + Edit, Delete, Slow Audio & History. */
public final class ChatBubbles {
 private static final int INK = 0xffffffff;
 private static final String HIST_KEY = "impara_chat_history_v1";

 static void detach(View v) { if (v != null && v.getParent() instanceof ViewGroup) ((ViewGroup) v.getParent()).removeView(v); }
 static ArrayList<View> children(ViewGroup g) { ArrayList<View> r = new ArrayList<>(); for (int i = 0; i < g.getChildCount(); i++) r.add(g.getChildAt(i)); return r; }
 static void buttons(View v, View table, List<Button> out) { if (v == table) return; if (v instanceof Button) out.add((Button) v); else if (v instanceof ViewGroup) for (View child : children((ViewGroup) v)) buttons(child, table, out); }

 public static TextView create(MainActivity a, String value, boolean user) {
  if (user) MoreCards546.user(a, value);
  TextView text = a.legacyMessage(value, user);
  if (a.page != 1 || a.chat == null) return text;
  View old = text;
  while (old.getParent() instanceof View && old.getParent() != a.chat) old = (View) old.getParent();
  if (old.getParent() != a.chat || !(old instanceof ViewGroup)) return text;
  int index = a.chat.indexOfChild(old);
  View table = old.findViewWithTag("study-columns");
  ArrayList<Button> all = new ArrayList<>();
  buttons(old, table, all);
  Button audio = null, translate = null, explain = null, columns = null, spanishAudio = null;
  for (Button b : all) {
   String d = String.valueOf(b.getContentDescription()), label = b.getText().toString();
   if (d.contains("columnas")) columns = b;
   else if (d.contains("traducción")) translate = b;
   else if (d.contains("español") && d.contains("Escuchar")) spanishAudio = b;
   else if (b.getTag() == text || d.contains("Paola") || d.contains("Escuchar o detener")) audio = b;
   else if (label.contains("Explicar") || label.contains("significado") || label.contains("Rayos X")) explain = b;
  }
  ArrayList<TextView> translations = new ArrayList<>();
  for (View v : children((ViewGroup) old)) if (v instanceof TextView && !(v instanceof Button) && v != text) translations.add((TextView) v);

  LinearLayout bubble = new LinearLayout(a);
  bubble.setOrientation(LinearLayout.VERTICAL);
  bubble.setPadding(a.dp(11), a.dp(9), a.dp(11), a.dp(4));
  bubble.setBackground(new WallStyle.Bubble(a, user));
  bubble.setTag(0x7e002602, user);
  bubble.setOnLongClickListener(v -> { showMessageMenu(a, bubble, text, user); return true; });

  LinearLayout body = new LinearLayout(a);
  body.setOrientation(LinearLayout.VERTICAL);
  detach(text);
  text.setTag(0x7e002601, true);
  text.setTextColor(a.prefs.getInt("chat_text_color", INK));
  text.setTextSize(a.prefs.getInt("chat_text_size", 16));
  text.setIncludeFontPadding(false);
  text.setPadding(0, 0, 0, 0);
  text.setLineSpacing(a.dp(3), 1);
  body.addView(text, new LinearLayout.LayoutParams(-1, -2));
  bubble.addView(body, new LinearLayout.LayoutParams(-1, -2));

  for (TextView t : translations) {
   detach(t);
   t.setTag(0x7e002601, true);
   t.setTextColor(a.prefs.getInt("chat_text_color", INK));
   t.setPadding(a.dp(7), a.dp(7), a.dp(7), a.dp(7));
   SofiaGlass.surface(a, t, 10, false, 0, false);
   bubble.addView(t, new LinearLayout.LayoutParams(-1, -2));
  }
  if (spanishAudio != null) { detach(spanishAudio); CompactSofia.fit(a, spanishAudio, null, 0); bubble.addView(spanishAudio, new LinearLayout.LayoutParams(-2, a.dp(36))); }
  if (table != null) { detach(table); bubble.addView(table, new LinearLayout.LayoutParams(-1, -2)); }

  LinearLayout actions = new LinearLayout(a);
  actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
  if (audio != null) ActionStrip543.add(a, actions, audio, "Oír", 46);
  if (translate != null) ActionStrip543.add(a, actions, translate, "Traducir", 64);
  if (explain != null) ActionStrip543.add(a, actions, explain, "Explicar", 62);
  if (columns != null) ActionStrip543.add(a, actions, columns, "Columnas", 68);
  Button shareButton = DeskUI.button(a, "Compartir", () -> ShareHub549.reply(a, text), false);
  ActionStrip543.add(a, actions, shareButton, "Compartir", 36);
  Button saveButton = DeskUI.button(a, "Guardar", () -> Repaso546.save(a, text.getText().toString(), ""), false);
  ActionStrip543.add(a, actions, saveButton, "Guardar", 36);
  Button menuButton = DeskUI.button(a, "⋮", () -> showMessageMenu(a, bubble, text, user), false);
  menuButton.setContentDescription("Opciones del mensaje");
  ActionStrip543.add(a, actions, menuButton, "◉", 32);

  HorizontalScrollView strip = new HorizontalScrollView(a);
  strip.setHorizontalScrollBarEnabled(false);
  strip.setFillViewport(false);
  strip.setClipToPadding(false);
  strip.addView(actions, new ViewGroup.LayoutParams(-2, -2));
  bubble.addView(strip, new LinearLayout.LayoutParams(-1, -2));

  a.chat.removeView(old);
  LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
  lp.setMargins(user ? a.dp(20) : 0, a.dp(4), user ? 0 : a.dp(12), a.dp(4));
  a.chat.addView(bubble, index, lp);
  a.root.post(() -> ButtonStudio.apply(a));
  StudyDesk.selection(a, text);
  return text;
 }

 private static void showMessageMenu(MainActivity a, LinearLayout bubble, TextView text, boolean user) {
  String[] opts = {
   "✏️ Editar mensaje",
   "🗑️ Borrar mensaje",
   "🐢 Escuchar lento (0.75x)",
   "🗂️ Historial de conversaciones",
   "🎨 Cambiar color de burbuja"
  };
  new AlertDialog.Builder(a)
   .setTitle("Impara l'italiano · Opciones")
   .setItems(opts, (d, which) -> {
    if (which == 0) editMessage(a, text, user);
    else if (which == 1) {
     if (bubble.getParent() instanceof ViewGroup) ((ViewGroup) bubble.getParent()).removeView(bubble);
     a.toast("Mensaje borrado");
    } else if (which == 2) {
     a.speak(text.getText().toString(), 0, 0.75f);
    } else if (which == 3) {
     showHistoryDialog(a);
    } else if (which == 4) {
     ChatLook542.palette(a, user);
    }
   })
   .show();
 }

 private static void editMessage(MainActivity a, TextView text, boolean user) {
  EditText et = new EditText(a);
  et.setText(text.getText().toString());
  et.setSelection(et.getText().length());
  AlertDialog.Builder b = new AlertDialog.Builder(a)
   .setTitle("Editar mensaje")
   .setView(et)
   .setPositiveButton("Guardar", (d, w) -> {
    String val = et.getText().toString().trim();
    if (!val.isEmpty()) { text.setText(val); a.toast("Mensaje editado"); }
   })
   .setNegativeButton("Cancelar", null);
  if (user) {
   b.setNeutralButton("Guardar y reenviar", (d, w) -> {
    String val = et.getText().toString().trim();
    if (!val.isEmpty()) { text.setText(val); a.ask(val); }
   });
  }
  b.show();
 }

 private static void showHistoryDialog(MainActivity a) {
  try {
   JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
   ArrayList<String> labels = new ArrayList<>();
   labels.add("➕ Guardar conversación actual");
   for (int i = arr.length() - 1; i >= 0; i--) {
    JSONObject o = arr.getJSONObject(i);
    labels.add("💬 " + o.optString("title", "Sesión") + " (" + o.optJSONArray("msgs").length() + " msgs)");
   }
   new AlertDialog.Builder(a)
    .setTitle("Historial de conversaciones")
    .setItems(labels.toArray(new String[0]), (d, which) -> {
     if (which == 0) saveCurrentChat(a);
     else openSavedChat(a, arr.length() - which);
    })
    .setNegativeButton("Cerrar", null)
    .show();
  } catch (Exception e) { a.toast("Error abriendo historial"); }
 }

 private static void saveCurrentChat(MainActivity a) {
  if (a.chat == null || a.chat.getChildCount() == 0) { a.toast("No hay mensajes para guardar"); return; }
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
   if (msgs.length() == 0) { a.toast("Chat vacío"); return; }
   JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
   JSONObject session = new JSONObject();
   String date = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date());
   session.put("title", date);
   session.put("msgs", msgs);
   arr.put(session);
   a.prefs.edit().putString(HIST_KEY, arr.toString()).apply();
   a.toast("Conversación guardada (" + msgs.length() + " mensajes)");
  } catch (Exception e) { a.toast("No se pudo guardar"); }
 }

 private static void openSavedChat(MainActivity a, int idx) {
  try {
   JSONArray arr = new JSONArray(a.prefs.getString(HIST_KEY, "[]"));
   JSONObject session = arr.getJSONObject(idx);
   JSONArray msgs = session.getJSONArray("msgs");
   StringBuilder sb = new StringBuilder();
   for (int i = 0; i < msgs.length(); i++) {
    JSONObject m = msgs.getJSONObject(i);
    sb.append(m.optBoolean("u") ? "Tú: " : "Sofía: ").append(m.optString("t")).append("\n\n");
   }
   new AlertDialog.Builder(a)
    .setTitle("Sesión " + session.optString("title"))
    .setMessage(sb.toString().trim())
    .setPositiveButton("Cargar en el chat", (d, w) -> {
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
    .show();
  } catch (Exception e) { a.toast("Error al cargar"); }
 }

 private static TextView findMainText(ViewGroup g) {
  for (int i = 0; i < g.getChildCount(); i++) {
   View c = g.getChildAt(i);
   if (c instanceof TextView && Boolean.TRUE.equals(c.getTag(0x7e002601))) return (TextView) c;
   if (c instanceof ViewGroup) { TextView r = findMainText((ViewGroup) c); if (r != null) return r; }
  }
  return null;
 }

 static void add(MainActivity a, LinearLayout row, Button b, int width, String label, int icon) {
  detach(b);
  CompactSofia.fit(a, b, label, width);
  if (icon != 0) SofiaGlass.action(a, b, icon);
  row.addView(b, new LinearLayout.LayoutParams(a.dp(width), a.dp(36)));
 }
}
