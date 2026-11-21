package com.germansilva.italianomaestro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class LiveGemma {
    private static final String PREF = "impara_gems_v1";
    private static final String KEY_LIST = "gems_json";
    private static final String KEY_ACTIVE = "active_gem";

    public static GradientDrawable neonPill(int fill, int stroke, int radiusDp, Context c) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fill);
        gd.setStroke(dp(c, 1.5f), stroke);
        gd.setCornerRadius(dp(c, radiusDp));
        return gd;
    }

    public static GradientDrawable neonCircle(int fill, int stroke, Context c) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fill);
        gd.setStroke(dp(c, 2f), stroke);
        return gd;
    }

    private static int dp(Context c, float v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics());
    }

    public static View buildFloatingBar(final MainActivity a) {
        LinearLayout bar = new LinearLayout(a);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        int p = dp(a, 6);
        bar.setPadding(p, p, p, p);

        TextView liveBtn = new TextView(a);
        liveBtn.setText("✦ Live");
        liveBtn.setTextColor(Color.parseColor("#E8F7FF"));
        liveBtn.setTextSize(12f);
        liveBtn.setTypeface(null, Typeface.BOLD);
        liveBtn.setPadding(dp(a, 12), dp(a, 6), dp(a, 12), dp(a, 6));
        liveBtn.setBackground(neonPill(Color.parseColor("#CC0D1B2A"), Color.parseColor("#38BDF8"), 20, a));
        liveBtn.setOnClickListener(v -> openLive(a));

        TextView gemsBtn = new TextView(a);
        gemsBtn.setText("💎 Gems");
        gemsBtn.setTextColor(Color.parseColor("#F3E8FF"));
        gemsBtn.setTextSize(12f);
        gemsBtn.setTypeface(null, Typeface.BOLD);
        gemsBtn.setPadding(dp(a, 12), dp(a, 6), dp(a, 12), dp(a, 6));
        gemsBtn.setBackground(neonPill(Color.parseColor("#CC1A102B"), Color.parseColor("#A855F7"), 20, a));
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-2, -2);
        gp.leftMargin = dp(a, 8);
        gemsBtn.setLayoutParams(gp);
        gemsBtn.setOnClickListener(v -> openGems(a));

        bar.addView(liveBtn);
        bar.addView(gemsBtn);
        return bar;
    }

    private static JSONArray loadGems(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = sp.getString(KEY_LIST, null);
        if (raw != null) {
            try { return new JSONArray(raw); } catch (Exception ignored) {}
        }
        JSONArray arr = new JSONArray();
        addGemObj(arr, "Profesora Sofía", "Eres Sofía, profesora de italiano amable y paciente. Responde en italiano natural y corrige errores con suavidad.");
        addGemObj(arr, "Mesero en Roma", "Eres un mesero simpático en una trattoria de Roma. Habla solo en italiano cotidiano de restaurante.");
        addGemObj(arr, "Corrector Estricto", "Corrige cada error gramatical o de vocabulario en español breve y luego muestra cómo lo diría un italiano nativo.");
        addGemObj(arr, "Amigo Italiano", "Eres un amigo italiano joven y relajado. Usa expresiones reales de la calle (allora, cioè, dai, magari).");
        addGemObj(arr, "Entrevista / Trabajo", "Simula una entrevista de trabajo formal en italiano en turismo/hospitalidad. Haz preguntas claras una por una.");
        sp.edit().putString(KEY_LIST, arr.toString()).apply();
        return arr;
    }

    private static void addGemObj(JSONArray arr, String name, String prompt) {
        try {
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("prompt", prompt);
            arr.put(o);
        } catch (Exception ignored) {}
    }

    public static JSONObject getActiveGem(Context c) {
        JSONArray arr = loadGems(c);
        int idx = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_ACTIVE, 0);
        if (idx < 0 || idx >= arr.length()) idx = 0;
        try { return arr.getJSONObject(idx); } catch (Exception e) { return new JSONObject(); }
    }

    public static void openGems(final MainActivity a) {
        final Dialog d = new Dialog(a, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#090D16"));
        root.setPadding(dp(a, 18), dp(a, 24), dp(a, 18), dp(a, 18));

        LinearLayout header = new LinearLayout(a);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(a);
        title.setText("💎 Gems · IA Personalizada");
        title.setTextColor(Color.WHITE);
        title.setTextSize(19f);
        title.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, -2, 1f);
        title.setLayoutParams(tp);

        TextView close = new TextView(a);
        close.setText("✕");
        close.setTextColor(Color.parseColor("#94A3B8"));
        close.setTextSize(18f);
        close.setPadding(dp(a, 12), dp(a, 6), dp(a, 12), dp(a, 6));
        close.setBackground(neonCircle(Color.parseColor("#1E293B"), Color.parseColor("#334155"), a));
        close.setOnClickListener(v -> d.dismiss());
        header.addView(title);
        header.addView(close);
        root.addView(header);

        TextView sub = new TextView(a);
        sub.setText("Elige una personalidad offline para Gemma o crea la tuya:");
        sub.setTextColor(Color.parseColor("#94A3B8"));
        sub.setTextSize(13f);
        sub.setPadding(0, dp(a, 8), 0, dp(a, 14));
        root.addView(sub);

        ScrollView sv = new ScrollView(a);
        LinearLayout list = new LinearLayout(a);
        list.setOrientation(LinearLayout.VERTICAL);
        sv.addView(list);
        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));

        final JSONArray gems = loadGems(a);
        final int activeIdx = a.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_ACTIVE, 0);

        for (int i = 0; i < gems.length(); i++) {
            final int idx = i;
            JSONObject g = gems.optJSONObject(i);
            if (g == null) continue;
            String gName = g.optString("name", "Gem");
            String gPrompt = g.optString("prompt", "");
            boolean isAct = (idx == activeIdx);

            LinearLayout card = new LinearLayout(a);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(a, 14), dp(a, 12), dp(a, 14), dp(a, 12));
            card.setBackground(neonPill(
                Color.parseColor(isAct ? "#172544" : "#111827"),
                Color.parseColor(isAct ? "#38BDF8" : "#263248"), 16, a));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
            cp.bottomMargin = dp(a, 10);
            card.setLayoutParams(cp);

            TextView tvN = new TextView(a);
            tvN.setText((isAct ? "✦ " : "◇ ") + gName + (isAct ? "  (Activo)" : ""));
            tvN.setTextColor(isAct ? Color.parseColor("#7DD3FC") : Color.WHITE);
            tvN.setTextSize(15f);
            tvN.setTypeface(null, Typeface.BOLD);

            TextView tvP = new TextView(a);
            tvP.setText(gPrompt);
            tvP.setTextColor(Color.parseColor("#94A3B8"));
            tvP.setTextSize(12.5f);
            tvP.setPadding(0, dp(a, 4), 0, 0);

            card.addView(tvN);
            card.addView(tvP);
            card.setOnClickListener(v -> {
                a.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putInt(KEY_ACTIVE, idx).apply();
                Toast.makeText(a, "Gem activo: " + gName, Toast.LENGTH_SHORT).show();
                d.dismiss();
            });
            list.addView(card);
        }

        TextView addBtn = new TextView(a);
        addBtn.setText("+ Crear nuevo Gem");
        addBtn.setGravity(Gravity.CENTER);
        addBtn.setTextColor(Color.WHITE);
        addBtn.setTextSize(14f);
        addBtn.setTypeface(null, Typeface.BOLD);
        addBtn.setPadding(dp(a, 16), dp(a, 12), dp(a, 16), dp(a, 12));
        addBtn.setBackground(neonPill(Color.parseColor("#2E1065"), Color.parseColor("#C084FC"), 24, a));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, -2);
        ap.topMargin = dp(a, 10);
        addBtn.setLayoutParams(ap);
        addBtn.setOnClickListener(v -> {
            d.dismiss();
            showCreateGemDialog(a);
        });
        root.addView(addBtn);

        d.setContentView(root);
        d.show();
    }

    private static void showCreateGemDialog(final MainActivity a) {
        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(a, 18), dp(a, 14), dp(a, 18), dp(a, 8));
        final EditText etName = new EditText(a);
        etName.setHint("Nombre del Gem (ej. Guía en Venecia)");
        final EditText etPrompt = new EditText(a);
        etPrompt.setHint("Instrucciones (ej. Habla italiano lento y corrígeme)");
        etPrompt.setMinLines(3);
        box.addView(etName);
        box.addView(etPrompt);

        new AlertDialog.Builder(a)
            .setTitle("Nuevo Gem Offline")
            .setView(box)
            .setPositiveButton("Guardar", (di, w) -> {
                String n = etName.getText().toString().trim();
                String p = etPrompt.getText().toString().trim();
                if (n.isEmpty() || p.isEmpty()) return;
                JSONArray arr = loadGems(a);
                addGemObj(arr, n, p);
                a.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
                    .putString(KEY_LIST, arr.toString())
                    .putInt(KEY_ACTIVE, arr.length() - 1).apply();
                Toast.makeText(a, "Gem creado y activado: " + n, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    public static void openLive(final MainActivity a) {
        final Dialog d = new Dialog(a, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout root = new FrameLayout(a);
        root.setBackgroundColor(Color.parseColor("#060A14"));

        final NeonOrbView orb = new NeonOrbView(a);
        root.addView(orb, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout ui = new LinearLayout(a);
        ui.setOrientation(LinearLayout.VERTICAL);
        ui.setGravity(Gravity.CENTER_HORIZONTAL);
        ui.setPadding(dp(a, 20), dp(a, 34), dp(a, 20), dp(a, 32));

        final JSONObject gem = getActiveGem(a);
        final TextView topBadge = new TextView(a);
        topBadge.setText("✦ Live Gemma · " + gem.optString("name", "Sofía"));
        topBadge.setTextColor(Color.parseColor("#BAE6FD"));
        topBadge.setTextSize(13f);
        topBadge.setTypeface(null, Typeface.BOLD);
        topBadge.setPadding(dp(a, 14), dp(a, 7), dp(a, 14), dp(a, 7));
        topBadge.setBackground(neonPill(Color.parseColor("#990F172A"), Color.parseColor("#38BDF8"), 22, a));
        topBadge.setOnClickListener(v -> { d.dismiss(); openGems(a); });
        ui.addView(topBadge);

        View spacer = new View(a);
        ui.addView(spacer, new LinearLayout.LayoutParams(-1, 0, 1f));

        final TextView statusTv = new TextView(a);
        statusTv.setText("Toca el micrófono para hablar en italiano");
        statusTv.setTextColor(Color.parseColor("#E2E8F0"));
        statusTv.setTextSize(16f);
        statusTv.setGravity(Gravity.CENTER);
        statusTv.setPadding(dp(a, 12), dp(a, 10), dp(a, 12), dp(a, 26));
        ui.addView(statusTv);

        LinearLayout controls = new LinearLayout(a);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);

        final boolean[] paused = {false};
        final SpeechRecognizer[] sr = {null};
        final Handler h = new Handler(Looper.getMainLooper());

        final Runnable startListen = new Runnable() {
            @Override public void run() {
                if (!d.isShowing() || paused[0]) return;
                try {
                    if (sr[0] == null) sr[0] = SpeechRecognizer.createSpeechRecognizer(a);
                    Intent in = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    in.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    in.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT");
                    in.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
                    in.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                    orb.setMode(1);
                    statusTv.setText("Escuchando...");
                    sr[0].setRecognitionListener(new RecognitionListener() {
                        @Override public void onReadyForSpeech(Bundle b) {}
                        @Override public void onBeginningOfSpeech() { orb.setMode(2); }
                        @Override public void onRmsChanged(float rms) { orb.setLevel(rms); }
                        @Override public void onBufferReceived(byte[] b) {}
                        @Override public void onEndOfSpeech() { orb.setMode(3); statusTv.setText("Pensando con Gemma..."); }
                        @Override public void onError(int err) {
                            orb.setMode(0);
                            statusTv.setText("Toca 🎙 para hablar de nuevo");
                        }
                        @Override public void onPartialResults(Bundle b) {
                            ArrayList<String> list = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            if (list != null && !list.isEmpty()) statusTv.setText("\"" + list.get(0) + "\"");
                        }
                        @Override public void onResults(Bundle b) {
                            ArrayList<String> list = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            if (list == null || list.isEmpty()) { orb.setMode(0); return; }
                            String said = list.get(0);
                            statusTv.setText("Tú: " + said + "\n\nSofía está respondiendo...");
                            orb.setMode(3);
                            JSONObject act = getActiveGem(a);
                            String fullPrompt = "[GEM: " + act.optString("name") + " - " + act.optString("prompt")
                                + "] [MODO LIVE: Responde en italiano natural en máximo 2 frases cortas, como en una llamada real.]\nUsuario dice: " + said;
                            sendToGemma(a, fullPrompt);
                            h.postDelayed(() -> {
                                if (d.isShowing() && !paused[0]) {
                                    orb.setMode(0);
                                    statusTv.setText("Toca 🎙 cuando Sofía termine de hablar");
                                }
                            }, 5500);
                        }
                        @Override public void onEvent(int t, Bundle b) {}
                    });
                    sr[0].startListening(in);
                } catch (Exception e) {
                    orb.setMode(0);
                    statusTv.setText("Micrófono no disponible: " + e.getMessage());
                }
            }
        };

        TextView pauseBtn = makeCircleBtn(a, "⏸", "#1E293B", "#64748B", 58);
        pauseBtn.setOnClickListener(v -> {
            paused[0] = !paused[0];
            pauseBtn.setText(paused[0] ? "▶" : "⏸");
            if (paused[0]) {
                try { if (sr[0] != null) sr[0].cancel(); } catch (Exception ignored) {}
                orb.setMode(0);
                statusTv.setText("Live en pausa");
            } else {
                startListen.run();
            }
        });

        TextView micBtn = makeCircleBtn(a, "🎙", "#0C4A6E", "#38BDF8", 74);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(dp(a, 74), dp(a, 74));
        mp.leftMargin = dp(a, 22);
        mp.rightMargin = dp(a, 22);
        micBtn.setLayoutParams(mp);
        micBtn.setOnClickListener(v -> {
            paused[0] = false;
            pauseBtn.setText("⏸");
            startListen.run();
        });

        TextView hangBtn = makeCircleBtn(a, "✕", "#7F1D1D", "#F87171", 58);
        hangBtn.setOnClickListener(v -> d.dismiss());

        controls.addView(pauseBtn);
        controls.addView(micBtn);
        controls.addView(hangBtn);
        ui.addView(controls);

        root.addView(ui, new FrameLayout.LayoutParams(-1, -1));
        d.setOnDismissListener(di -> {
            orb.stop();
            try { if (sr[0] != null) sr[0].destroy(); } catch (Exception ignored) {}
        });
        d.setContentView(root);
        d.show();
        h.postDelayed(startListen, 350);
    }

    private static TextView makeCircleBtn(Context c, String txt, String bg, String stroke, int sizeDp) {
        TextView b = new TextView(c);
        b.setText(txt);
        b.setTextColor(Color.WHITE);
        b.setTextSize(sizeDp > 65 ? 24f : 19f);
        b.setGravity(Gravity.CENTER);
        b.setBackground(neonCircle(Color.parseColor(bg), Color.parseColor(stroke), c));
        b.setLayoutParams(new LinearLayout.LayoutParams(dp(c, sizeDp), dp(c, sizeDp)));
        return b;
    }

    private static void sendToGemma(MainActivity a, String prompt) {
        try {
            Class<?> cls = Class.forName("com.germansilva.italianomaestro.StudyDesk");
            Method m = cls.getDeclaredMethod("ask", MainActivity.class, String.class);
            m.setAccessible(true);
            m.invoke(null, a, prompt);
        } catch (Exception e) {
            Toast.makeText(a, "Error enviando a Gemma: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    static class NeonOrbView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float phase = 0f, level = 0f;
        private int mode = 0; // 0=idle, 1=listen, 2=voice, 3=think
        private boolean alive = true;

        NeonOrbView(Context c) { super(c); }
        void setMode(int m) { mode = m; }
        void setLevel(float rms) { level = Math.max(0f, Math.min(1f, rms / 10f)); }
        void stop() { alive = false; }

        @Override protected void onDraw(Canvas cv) {
            int w = getWidth(), h = getHeight();
            float cx = w * 0.5f, cy = h * 0.42f;
            float baseR = Math.min(w, h) * 0.22f;
            phase += (mode == 3 ? 0.08f : 0.035f);
            float pulse = (float) Math.sin(phase) * (mode == 2 ? 18f + level * 26f : 10f);

            p.setShader(new RadialGradient(cx, cy, baseR * 2.2f,
                new int[]{mode == 3 ? 0x55A855F7 : 0x5538BDF8, 0x220EA5E9, 0x00000000},
                null, Shader.TileMode.CLAMP));
            cv.drawCircle(cx, cy, baseR * 2.2f + pulse, p);

            p.setShader(new LinearGradient(cx - baseR, cy - baseR, cx + baseR, cy + baseR,
                mode == 3 ? 0xFFC084FC : 0xFF38BDF8,
                mode == 2 ? 0xFF22D3EE : 0xFF6366F1,
                Shader.TileMode.CLAMP));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(6f);
            cv.drawCircle(cx, cy, baseR + pulse * 0.6f, p);

            p.setStrokeWidth(3f);
            p.setAlpha(150);
            cv.drawCircle(cx, cy, baseR * 0.78f - pulse * 0.4f, p);
            p.setStyle(Paint.Style.FILL);
            p.setAlpha(255);

            if (alive) postInvalidateOnAnimation();
        }
    }
}
