package com.github.tvbox.osc.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.ToastUtils;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.IJKCode;
import com.github.tvbox.osc.player.EXOmPlayer;
import com.github.tvbox.osc.player.IjkmPlayer;
import com.github.tvbox.osc.player.render.SurfaceRenderViewFactory;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import xyz.doikki.videoplayer.aliplayer.AliyunMediaPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.PlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.render.PlayerViewRenderViewFactory;
import xyz.doikki.videoplayer.render.RenderViewFactory;
import xyz.doikki.videoplayer.render.TextureRenderViewFactory;

public class PlayerHelper {
    public static void updateCfg(VideoView videoView, JSONObject playerCfg) {
        updateCfg(videoView, playerCfg, -1);
    }

    public static void updateCfg(VideoView videoView, JSONObject playerCfg, int forcePlayerType) {
        int playerType = Hawk.get(HawkConfig.PLAY_TYPE, 2);
        int renderType = Hawk.get(HawkConfig.PLAY_RENDER, 0);
        String ijkCode = Hawk.get(HawkConfig.IJK_CODEC, "硬解码");
        int scale = Hawk.get(HawkConfig.PLAY_SCALE, 0);
        try {
            if (playerCfg != null) {
                if (playerCfg.has("pl")) playerType = playerCfg.getInt("pl");
                if (playerCfg.has("ijk")) ijkCode = playerCfg.getString("ijk");
                if (playerCfg.has("sc")) scale = playerCfg.getInt("sc");
                if (playerCfg.has("pr")) renderType = playerCfg.getInt("pr");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (forcePlayerType >= 0) playerType = forcePlayerType;
        
        Hawk.put(HawkConfig.PLAY_TYPE, playerType);
        Hawk.put(HawkConfig.IJK_CODEC, ijkCode);
        Hawk.put(HawkConfig.PLAY_RENDER, renderType);

        IJKCode codec = ApiConfig.get().getIJKCodec(ijkCode);
        PlayerFactory playerFactory = createPlayerFactory(playerType, codec);
        RenderViewFactory renderViewFactory = createRenderFactory(playerType, renderType);

        videoView.setPlayerFactory(playerFactory);
        videoView.setRenderViewFactory(renderViewFactory);
        videoView.setScreenScaleType(scale);
    }

    public static void updateCfg(VideoView videoView) {
        int playType = Hawk.get(HawkConfig.PLAY_TYPE, 2);
        int renderType = Hawk.get(HawkConfig.PLAY_RENDER, 0);
        String ijkCode = Hawk.get(HawkConfig.IJK_CODEC, "硬解码");
        IJKCode codec = ApiConfig.get().getIJKCodec(ijkCode);

        PlayerFactory playerFactory = createPlayerFactory(playType, codec);
        RenderViewFactory renderViewFactory = createRenderFactory(playType, renderType);
        
        videoView.setPlayerFactory(playerFactory);
        videoView.setRenderViewFactory(renderViewFactory);
    }

    private static PlayerFactory createPlayerFactory(int playerType, IJKCode codec) {
        PlayerFactory playerFactory;
        switch (playerType) {
            case 1:
                playerFactory = new PlayerFactory<IjkmPlayer>() {
                    @Override
                    public IjkmPlayer createPlayer(Context context) {
                        return new IjkmPlayer(context, codec);
                    }
                };
                break;
            case 2:
                playerFactory = new PlayerFactory<EXOmPlayer>() {
                    @Override
                    public EXOmPlayer createPlayer(Context context) {
                        return new EXOmPlayer(context);
                    }
                };
                break;
            case 3:
                playerFactory = AliyunMediaPlayerFactory.create();
                break;
            case 10: // MX
                playerFactory = new PlayerFactory<IjkmPlayer>() {
                    @Override
                    public IjkmPlayer createPlayer(Context context) {
                        return new IjkmPlayer(context, codec != null ? codec : ApiConfig.get().getIJKCodec("硬解码"));
                    }
                };
                break;
            case 11: // Reex
                playerFactory = new PlayerFactory<EXOmPlayer>() {
                    @Override
                    public EXOmPlayer createPlayer(Context context) {
                        return new EXOmPlayer(context);
                    }
                };
                break;
            case 12: // Kodi
                playerFactory = new PlayerFactory<IjkmPlayer>() {
                    @Override
                    public IjkmPlayer createPlayer(Context context) {
                        return new IjkmPlayer(context, ApiConfig.get().getIJKCodec("软解码"));
                    }
                };
                break;
            case 0:
            default:
                playerFactory = AndroidMediaPlayerFactory.create();
                break;
        }
        return playerFactory;
    }

    private static RenderViewFactory createRenderFactory(int playerType, int renderType) {
        RenderViewFactory renderViewFactory;
        if (playerType == 2 || playerType == 11) {
            renderViewFactory = PlayerViewRenderViewFactory.create(renderType);
        } else {
            switch (renderType) {
                case 1:
                    renderViewFactory = SurfaceRenderViewFactory.create();
                    break;
                case 0:
                default:
                    renderViewFactory = TextureRenderViewFactory.create();
                    break;
            }
        }
        return renderViewFactory;
    }

    public static void init() {
        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    public static String getPlayerName(int playType) {
        if (playType == 1) {
            return "IJK";
        } else if (playType == 2) {
            return "Exo";
        } else if (playType == 3) {
            return "阿里";
        } else if (playType == 10) {
            return "MX";
        } else if (playType == 11) {
            return "Reex";
        } else if (playType == 12) {
            return "Kodi";
        } else {
            return "系统";
        }
    }

    public static String[] getAllPlayerNames() {
        return new String[]{"系统", "IJK", "Exo", "阿里", "MX", "Reex", "Kodi"};
    }

    public static int[] getAllPlayerTypes() {
        return new int[]{0, 1, 2, 3, 10, 11, 12};
    }

    public static String getRenderName(int renderType) {
        if (renderType == 1) {
            return "SurfaceView";
        } else {
            return "TextureView";
        }
    }

    public static String getScaleName(int screenScaleType) {
        String scaleText = "默认";
        switch (screenScaleType) {
            case VideoView.SCREEN_SCALE_DEFAULT:
                scaleText = "默认";
                break;
            case VideoView.SCREEN_SCALE_16_9:
                scaleText = "16:9";
                break;
            case VideoView.SCREEN_SCALE_4_3:
                scaleText = "4:3";
                break;
            case VideoView.SCREEN_SCALE_MATCH_PARENT:
                scaleText = "填充";
                break;
            case VideoView.SCREEN_SCALE_ORIGINAL:
                scaleText = "原始";
                break;
            case VideoView.SCREEN_SCALE_CENTER_CROP:
                scaleText = "裁剪";
                break;
        }
        return scaleText;
    }

    public static String getRootCauseMessage(Throwable th) {
        for (int i=0; i<10; i++) {
            if (th.getCause() == null) return th.getLocalizedMessage();
            else th = th.getCause();
        }
        return th.getLocalizedMessage();
    }
}
