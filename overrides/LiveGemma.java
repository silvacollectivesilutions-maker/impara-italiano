package com.germansilva.italianomaestro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class LiveGemma {
    private static final String PREF = "impara_gems_v2";
    private static final String KEY_LIST = "gems_json";
    private static final String KEY_ACTIVE = "active_gem";

    public static GradientDrawable glassPill(int fill, int stroke, int radiusDp, Context c) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fill);
        gd.setStroke(dp(c, 1.2f), stroke);
        gd.setCornerRadius(dp(c, radiusDp));
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
        liveBtn.setText("Live");
        liveBtn.setTextColor(Color.parseColor("#E0F2FE"));
        liveBtn.setTextSize(12f);
        liveBtn.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        liveBtn.setPadding(dp(a, 14), dp(a, 6), dp(a, 14), dp(a, 6));
        liveBtn.setBackground(glassPill(Color.parseColor("#CC0A192F"), Color.parseColor("#38BDF8"), 20, a));
        liveBtn.setOnClickListener(v -> openLive(a));

        TextView gemsBtn = new TextView(a);
        gemsBtn.setText("Gems");
        gemsBtn.setTextColor(Color.parseColor("#F3E8FF"));
        gemsBtn.setTextSize(12f);
        gemsBtn.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        gemsBtn.setPadding(dp(a, 14), dp(a, 6), dp(a, 14), dp(a, 6));
        gemsBtn.setBackground(glassPill(Color.parseColor("#CC160D29"), Color.parseColor("#A855F7"), 20, a));
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
        addGemObj(arr, "Chiara · Tutora Live", "Eres Chiara, una tutora italiana joven, dinámica y natural de Milán. Conversa en italiano fluido en respuestas cortas tipo llamada real y corrige con amabilidad.");
        addGemObj(arr, "Mesero en Roma", "Eres Marco, mesero en una trattoria de Roma. Habla solo en italiano cotidiano de restaurante.");
        addGemObj(arr, "Corrector Estricto", "Corrige cada error gramatical o de pronunciación en español breve y luego di cómo lo diría un italiano nativo.");
        addGemObj(arr, "Amigo Italiano", "Eres Luca, un amigo italiano relajado. Usa expresiones reales de la calle (allora, cioè, dai, magari).");
        addGemObj(arr, "Entrevista / Trabajo", "Simula una entrevista de trabajo formal en italiano en turismo y cruceros. Haz una pregunta clara a la vez.");
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
        root.setBackgroundColor(Color.parseColor("#070B14"));
        root.setPadding(dp(a, 18), dp(a, 24), dp(a, 18), dp(a, 18));

        LinearLayout header = new LinearLayout(a);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(a);
        title.setText("Gems · Tutores Live");
        title.setTextColor(Color.WHITE);
        title.setTextSize(19f);
        title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));

        VectorIconBtn close = new VectorIconBtn(a, 2, Color.parseColor("#141C2B"), Color.parseColor("#334155"));
        close.setLayoutParams(new LinearLayout.LayoutParams(dp(a, 42), dp(a, 42)));
        close.setOnClickListener(v -> d.dismiss());
        header.addView(title);
        header.addView(close);
        root.addView(header);

        TextView sub = new TextView(a);
        sub.setText("Elige tu tutora o personalidad offline para Live Gemma:");
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
            card.setPadding(dp(a, 16), dp(a, 14), dp(a, 16), dp(a, 14));
            card.setBackground(glassPill(
                Color.parseColor(isAct ? "#13223D" : "#0F1623"),
                Color.parseColor(isAct ? "#38BDF8" : "#1E293B"), 16, a));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
            cp.bottomMargin = dp(a, 10);
            card.setLayoutParams(cp);

            TextView tvN = new TextView(a);
            tvN.setText(gName + (isAct ? "  · Activo" : ""));
            tvN.setTextColor(isAct ? Color.parseColor("#7DD3FC") : Color.WHITE);
            tvN.setTextSize(15f);
            tvN.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

            TextView tvP = new TextView(a);
            tvP.setText(gPrompt);
            tvP.setTextColor(Color.parseColor("#94A3B8"));
            tvP.setTextSize(12.5f);
            tvP.setPadding(0, dp(a, 4), 0, 0);

            card.addView(tvN);
            card.addView(tvP);
            card.setOnClickListener(v -> {
                a.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putInt(KEY_ACTIVE, idx).apply();
                Toast.makeText(a, "Tutor activo: " + gName, Toast.LENGTH_SHORT).show();
                d.dismiss();
            });
            list.addView(card);
        }

        TextView addBtn = new TextView(a);
        addBtn.setText("+ Crear nuevo Gem");
        addBtn.setGravity(Gravity.CENTER);
        addBtn.setTextColor(Color.WHITE);
        addBtn.setTextSize(14f);
        addBtn.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        addBtn.setPadding(dp(a, 16), dp(a, 13), dp(a, 16), dp(a, 13));
        addBtn.setBackground(glassPill(Color.parseColor("#24124D"), Color.parseColor("#A855F7"), 24, a));
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
        etName.setHint("Nombre del Gem (ej. Elena · Guía en Venecia)");
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
        if (a.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            a.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 904);
            Toast.makeText(a, "Acepta el permiso de micrófono y vuelve a tocar Live", Toast.LENGTH_LONG).show();
            return;
        }

        final Dialog d = new Dialog(a, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout root = new FrameLayout(a);
        root.setBackgroundColor(Color.parseColor("#04070F"));

        final GeminiAuroraView aurora = new GeminiAuroraView(a);
        root.addView(aurora, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout ui = new LinearLayout(a);
        ui.setOrientation(LinearLayout.VERTICAL);
        ui.setGravity(Gravity.CENTER_HORIZONTAL);
        ui.setPadding(dp(a, 20), dp(a, 34), dp(a, 20), dp(a, 32));

        final JSONObject gem = getActiveGem(a);
        final String tutorName = gem.optString("name", "Chiara · Tutora Live");

        final TextView topBadge = new TextView(a);
        topBadge.setText("Live Gemma  ·  " + tutorName);
        topBadge.setTextColor(Color.parseColor("#E0F2FE"));
        topBadge.setTextSize(13f);
        topBadge.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        topBadge.setPadding(dp(a, 18), dp(a, 8), dp(a, 18), dp(a, 8));
        topBadge.setBackground(glassPill(Color.parseColor("#66101B2E"), Color.parseColor("#38BDF8"), 24, a));
        topBadge.setOnClickListener(v -> { d.dismiss(); openGems(a); });
        ui.addView(topBadge);

        // Retrato fotorealista circular de la tutora Chiara
        LinearLayout tutorCenter = new LinearLayout(a);
        tutorCenter.setOrientation(LinearLayout.VERTICAL);
        tutorCenter.setGravity(Gravity.CENTER_HORIZONTAL);
        tutorCenter.setPadding(0, dp(a, 22), 0, dp(a, 12));

        String initial = tutorName.isEmpty() ? "C" : tutorName.substring(0, 1).toUpperCase();
        TutorPhotoView photoView = new TutorPhotoView(a, "sofia84/chiara.jpg", initial);
        photoView.setLayoutParams(new LinearLayout.LayoutParams(dp(a, 138), dp(a, 138)));

        TextView tutorTitle = new TextView(a);
        tutorTitle.setText(tutorName);
        tutorTitle.setGravity(Gravity.CENTER);
        tutorTitle.setTextColor(Color.WHITE);
        tutorTitle.setTextSize(18f);
        tutorTitle.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        tutorTitle.setPadding(0, dp(a, 12), 0, dp(a, 2));

        TextView tutorSub = new TextView(a);
        tutorSub.setText("Conversación en vivo · 100% Offline");
        tutorSub.setGravity(Gravity.CENTER);
        tutorSub.setTextColor(Color.parseColor("#94A3B8"));
        tutorSub.setTextSize(12.5f);

        tutorCenter.addView(photoView);
        tutorCenter.addView(tutorTitle);
        tutorCenter.addView(tutorSub);
        ui.addView(tutorCenter);

        // Caja de subtítulos en vivo (muestra lo que dices y lo que responde Chiara)
        ScrollView replyScroll = new ScrollView(a);
        LinearLayout.LayoutParams rsp = new LinearLayout.LayoutParams(-1, 0, 1f);
        rsp.topMargin = dp(a, 10);
        rsp.bottomMargin = dp(a, 14);
        replyScroll.setLayoutParams(rsp);
        replyScroll.setBackground(glassPill(Color.parseColor("#770B1324"), Color.parseColor("#3338BDF8"), 18, a));
        replyScroll.setPadding(dp(a, 16), dp(a, 14), dp(a, 16), dp(a, 14));

        final TextView replyTv = new TextView(a);
        replyTv.setText("Ciao! Sono Chiara. Dimmi qualcosa in italiano...");
        replyTv.setTextColor(Color.parseColor("#F1F5F9"));
        replyTv.setTextSize(16f);
        replyTv.setLineSpacing(dp(a, 4), 1f);
        replyTv.setGravity(Gravity.CENTER_HORIZONTAL);
        replyScroll.addView(replyTv);
        ui.addView(replyScroll);

        final TextView statusTv = new TextView(a);
        statusTv.setText("Escuchando...");
        statusTv.setTextColor(Color.parseColor("#BAE6FD"));
        statusTv.setTextSize(13.5f);
        statusTv.setGravity(Gravity.CENTER);
        statusTv.setPadding(dp(a, 12), dp(a, 4), dp(a, 12), dp(a, 18));
        ui.addView(statusTv);

        LinearLayout controls = new LinearLayout(a);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);

        final boolean[] paused = {false};
        final SpeechRecognizer[] sr = {null};
        final Handler h = new Handler(Looper.getMainLooper());
        final String[] lastChatSnapshot = {extractLatestReply(a.getWindow().getDecorView(), "")};

        final Runnable pollReply = new Runnable() {
            int ticks = 0;
            @Override public void run() {
                if (!d.isShowing()) return;
                String latest = extractLatestReply(a.getWindow().getDecorView(), lastChatSnapshot[0]);
                if (latest != null && !latest.isEmpty() && !latest.equals(lastChatSnapshot[0])) {
                    replyTv.setText(latest);
                    replyScroll.post(() -> replyScroll.fullScroll(View.FOCUS_DOWN));
                }
                ticks++;
                if (ticks < 45) {
                    h.postDelayed(this, 600);
                } else {
                    ticks = 0;
                    aurora.setMode(0);
                    statusTv.setText("Toca el micrófono para continuar");
                }
            }
        };

        final Runnable startListen = new Runnable() {
            @Override public void run() {
                if (!d.isShowing() || paused[0]) return;
                try {
                    if (sr[0] != null) { try { sr[0].destroy(); } catch (Exception ignored) {} }
                    sr[0] = SpeechRecognizer.createSpeechRecognizer(a);
                    Intent in = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    in.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    in.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT");
                    in.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                    in.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    aurora.setMode(1);
                    statusTv.setText("Escuchando...");
                    sr[0].setRecognitionListener(new RecognitionListener() {
                        @Override public void onReadyForSpeech(Bundle b) { aurora.setMode(1); }
                        @Override public void onBeginningOfSpeech() { aurora.setMode(2); }
                        @Override public void onRmsChanged(float rms) { aurora.setLevel(rms); }
                        @Override public void onBufferReceived(byte[] b) {}
                        @Override public void onEndOfSpeech() { aurora.setMode(3); statusTv.setText("Pensando..."); }
                        @Override public void onError(int err) {
                            aurora.setMode(0);
                            statusTv.setText("Toca el micrófono para hablar");
                        }
                        @Override public void onPartialResults(Bundle b) {
                            ArrayList<String> list = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            if (list != null && !list.isEmpty()) statusTv.setText("Tú: " + list.get(0));
                        }
                        @Override public void onResults(Bundle b) {
                            ArrayList<String> list = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            if (list == null || list.isEmpty()) { aurora.setMode(0); return; }
                            String said = list.get(0);
                            statusTv.setText("Tú: " + said);
                            replyTv.setText(tutorName + " está pensando...");
                            aurora.setMode(3);
                            lastChatSnapshot[0] = extractLatestReply(a.getWindow().getDecorView(), "");
                            JSONObject act = getActiveGem(a);
                            String fullPrompt = "[GEM: " + act.optString("name") + " - " + act.optString("prompt")
                                + "] [MODO LIVE: Responde en italiano natural en máximo 2 frases cortas.]\nUsuario: " + said;
                            sendAndAutoSubmit(a, fullPrompt, h);
                            h.removeCallbacks(pollReply);
                            h.postDelayed(pollReply, 900);
                        }
                        @Override public void onEvent(int t, Bundle b) {}
                    });
                    sr[0].startListening(in);
                } catch (Exception e) {
                    aurora.setMode(0);
                    statusTv.setText("Error de micrófono: " + e.getMessage());
                }
            }
        };

        final VectorIconBtn pauseBtn = new VectorIconBtn(a, 0, Color.parseColor("#161F30"), Color.parseColor("#334155"));
        pauseBtn.setLayoutParams(new LinearLayout.LayoutParams(dp(a, 60), dp(a, 60)));
        pauseBtn.setOnClickListener(v -> {
            paused[0] = !paused[0];
            pauseBtn.setIconType(paused[0] ? 3 : 0);
            if (paused[0]) {
                try { if (sr[0] != null) sr[0].cancel(); } catch (Exception ignored) {}
                aurora.setMode(0);
                statusTv.setText("En pausa");
            } else {
                startListen.run();
            }
        });

        VectorIconBtn micBtn = new VectorIconBtn(a, 1, Color.parseColor("#0D3B66"), Color.parseColor("#38BDF8"));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(dp(a, 76), dp(a, 76));
        mp.leftMargin = dp(a, 24);
        mp.rightMargin = dp(a, 24);
        micBtn.setLayoutParams(mp);
        micBtn.setOnClickListener(v -> {
            paused[0] = false;
            pauseBtn.setIconType(0);
            startListen.run();
        });

        VectorIconBtn hangBtn = new VectorIconBtn(a, 2, Color.parseColor("#7F1D1D"), Color.parseColor("#EF4444"));
        hangBtn.setLayoutParams(new LinearLayout.LayoutParams(dp(a, 60), dp(a, 60)));
        hangBtn.setOnClickListener(v -> d.dismiss());

        controls.addView(pauseBtn);
        controls.addView(micBtn);
        controls.addView(hangBtn);
        ui.addView(controls);

        root.addView(ui, new FrameLayout.LayoutParams(-1, -1));
        d.setOnDismissListener(di -> {
            aurora.stop();
            h.removeCallbacksAndMessages(null);
            try { if (sr[0] != null) sr[0].destroy(); } catch (Exception ignored) {}
        });
        d.setContentView(root);
        d.show();
        h.postDelayed(startListen, 300);
    }

    private static String extractLatestReply(View root, String ignore) {
        final String[] best = {""};
        scanTexts(root, best, ignore);
        return best[0];
    }

    private static void scanTexts(View v, String[] best, String ignore) {
        if (v instanceof EditText) return;
        if (v instanceof TextView) {
            CharSequence cs = ((TextView) v).getText();
            if (cs != null) {
                String t = cs.toString().trim();
                if (t.length() > 14
                    && !t.contains("[GEM:")
                    && !t.contains("Gemma 4")
                    && !t.equals("Inicio") && !t.equals("Estudiar") && !t.equals("Sofía") && !t.equals("Más")
                    && !t.equals(ignore)) {
                    best[0] = t;
                }
            }
        } else if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scanTexts(vg.getChildAt(i), best, ignore);
            }
        }
    }

    private static void sendAndAutoSubmit(final MainActivity a, String prompt, Handler h) {
        try {
            Class<?> cls = Class.forName("com.germansilva.italianomaestro.StudyDesk");
            Method m = cls.getDeclaredMethod("ask", MainActivity.class, String.class);
            m.setAccessible(true);
            m.invoke(null, a, prompt);
            h.postDelayed(() -> clickSendButton(a.getWindow().getDecorView()), 200);
        } catch (Exception e) {
            Toast.makeText(a, "Error enviando a Gemma: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean clickSendButton(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            boolean hasEdit = false;
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (vg.getChildAt(i) instanceof EditText) {
                    hasEdit = true;
                    break;
                }
            }
            if (hasEdit && vg.getChildCount() > 1) {
                View last = vg.getChildAt(vg.getChildCount() - 1);
                if (last.isClickable() && last.getVisibility() == View.VISIBLE) {
                    last.performClick();
                    return true;
                }
            }
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (clickSendButton(vg.getChildAt(i))) return true;
            }
        }
        return false;
    }

    static class TutorPhotoView extends View {
        private Bitmap bmp;
        private final String fallback;
        private final Paint imgP = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint ringP = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint txtP = new Paint(Paint.ANTI_ALIAS_FLAG);

        TutorPhotoView(Context c, String assetPath, String fallback) {
            super(c);
            this.fallback = fallback;
            try (InputStream is = c.getAssets().open(assetPath)) {
                bmp = BitmapFactory.decodeStream(is);
            } catch (Exception ignored) {}
            ringP.setStyle(Paint.Style.STROKE);
            ringP.setStrokeWidth(dp(c, 2.5f));
            ringP.setColor(Color.parseColor("#38BDF8"));
            txtP.setColor(Color.parseColor("#E0F2FE"));
            txtP.setTextAlign(Paint.Align.CENTER);
            txtP.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }

        @Override protected void onDraw(Canvas cv) {
            float w = getWidth(), h = getHeight(), cx = w * 0.5f, cy = h * 0.5f;
            float r = Math.min(cx, cy) - ringP.getStrokeWidth() * 2f;
            if (bmp != null) {
                BitmapShader sh = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                float scale = Math.max(w / bmp.getWidth(), h / bmp.getHeight());
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                m.postTranslate((w - bmp.getWidth() * scale) * 0.5f, (h - bmp.getHeight() * scale) * 0.5f);
                sh.setLocalMatrix(m);
                imgP.setShader(sh);
                cv.drawCircle(cx, cy, r, imgP);
            } else {
                imgP.setShader(null);
                imgP.setColor(Color.parseColor("#13223D"));
                cv.drawCircle(cx, cy, r, imgP);
                txtP.setTextSize(r * 0.8f);
                cv.drawText(fallback, cx, cy + r * 0.28f, txtP);
            }
            cv.drawCircle(cx, cy, r, ringP);
        }
    }

    static class VectorIconBtn extends View {
        private int iconType;
        private final Paint bgP = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stP = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint icP = new Paint(Paint.ANTI_ALIAS_FLAG);

        VectorIconBtn(Context c, int type, int bg, int stroke) {
            super(c);
            this.iconType = type;
            bgP.setColor(bg);
            stP.setColor(stroke);
            stP.setStyle(Paint.Style.STROKE);
            stP.setStrokeWidth(dp(c, 1.5f));
            icP.setColor(Color.WHITE);
            icP.setStrokeCap(Paint.Cap.ROUND);
            icP.setStrokeJoin(Paint.Join.ROUND);
        }

        void setIconType(int t) { iconType = t; invalidate(); }

        @Override protected void onDraw(Canvas cv) {
            float cx = getWidth() * 0.5f, cy = getHeight() * 0.5f, r = Math.min(cx, cy) - stP.getStrokeWidth();
            cv.drawCircle(cx, cy, r, bgP);
            cv.drawCircle(cx, cy, r, stP);
            float u = r * 0.36f;
            if (iconType == 0) {
                icP.setStyle(Paint.Style.FILL);
                cv.drawRoundRect(cx - u * 0.7f, cy - u, cx - u * 0.22f, cy + u, 4, 4, icP);
                cv.drawRoundRect(cx + u * 0.22f, cy - u, cx + u * 0.7f, cy + u, 4, 4, icP);
            } else if (iconType == 1) {
                icP.setStyle(Paint.Style.STROKE);
                icP.setStrokeWidth(r * 0.08f);
                cv.drawRoundRect(cx - u * 0.42f, cy - u * 1.05f, cx + u * 0.42f, cy + u * 0.15f, u * 0.42f, u * 0.42f, icP);
                cv.drawArc(cx - u * 0.85f, cy - u * 0.55f, cx + u * 0.85f, cy + u * 0.65f, 0, 180, false, icP);
                cv.drawLine(cx, cy + u * 0.65f, cx, cy + u * 1.1f, icP);
                cv.drawLine(cx - u * 0.45f, cy + u * 1.1f, cx + u * 0.45f, cy + u * 1.1f, icP);
            } else if (iconType == 2) {
                icP.setStyle(Paint.Style.STROKE);
                icP.setStrokeWidth(r * 0.09f);
                cv.drawLine(cx - u * 0.7f, cy - u * 0.7f, cx + u * 0.7f, cy + u * 0.7f, icP);
                cv.drawLine(cx + u * 0.7f, cy - u * 0.7f, cx - u * 0.7f, cy + u * 0.7f, icP);
            } else if (iconType == 3) {
                icP.setStyle(Paint.Style.FILL);
                Path p = new Path();
                p.moveTo(cx - u * 0.5f, cy - u * 0.9f);
                p.lineTo(cx + u * 0.9f, cy);
                p.lineTo(cx - u * 0.5f, cy + u * 0.9f);
                p.close();
                cv.drawPath(p, icP);
            }
        }
    }

    static class GeminiAuroraView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private float t = 0f, smoothLvl = 0.15f, targetLvl = 0.15f;
        private int mode = 0;
        private boolean alive = true;

        GeminiAuroraView(Context c) { super(c); }
        void setMode(int m) { mode = m; if (m == 0) targetLvl = 0.08f; else if (m == 1) targetLvl = 0.28f; else if (m == 3) targetLvl = 0.55f; }
        void setLevel(float rms) { targetLvl = Math.max(0.2f, Math.min(1f, (rms + 2f) / 10f)); }
        void stop() { alive = false; }

        @Override protected void onDraw(Canvas cv) {
            int w = getWidth(), h = getHeight();
            t += (mode == 3 ? 0.065f : 0.032f);
            smoothLvl += (targetLvl - smoothLvl) * 0.12f;

            float glowY = h * 0.88f;
            float glowR = Math.max(w, h) * (0.52f + smoothLvl * 0.18f);
            int c1 = (mode == 3) ? 0x668B5CF6 : 0x660EA5E9;
            int c2 = (mode == 2) ? 0x4422D3EE : 0x333B82F6;
            p.setShader(new RadialGradient(w * 0.5f, glowY, glowR, new int[]{c1, c2, 0x00000000}, null, Shader.TileMode.CLAMP));
            cv.drawRect(0, h * 0.35f, w, h, p);

            drawWave(cv, w, h, h * 0.72f, 26f + smoothLvl * 48f, 1.3f, t, 0xAA38BDF8, 0x0038BDF8);
            drawWave(cv, w, h, h * 0.76f, 22f + smoothLvl * 42f, 1.8f, -t * 1.2f, 0x99818CF8, 0x00818CF8);
            drawWave(cv, w, h, h * 0.80f, 18f + smoothLvl * 34f, 2.3f, t * 1.5f, 0x8822D3EE, 0x0022D3EE);

            if (alive) postInvalidateOnAnimation();
        }

        private void drawWave(Canvas cv, int w, int h, float baseY, float amp, float freq, float phase, int topCol, int botCol) {
            path.reset();
            path.moveTo(0, h);
            for (int x = 0; x <= w; x += 18) {
                float nx = (float) x / w;
                float env = (float) Math.sin(nx * Math.PI);
                float y = baseY + (float) Math.sin(nx * Math.PI * 2f * freq + phase) * amp * env;
                path.lineTo(x, y);
            }
            path.lineTo(w, h);
            path.close();
            p.setShader(new LinearGradient(0, baseY - amp, 0, h, topCol, botCol, Shader.TileMode.CLAMP));
            cv.drawPath(path, p);
        }
    }
}
