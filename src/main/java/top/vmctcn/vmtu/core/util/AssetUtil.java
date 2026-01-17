package top.vmctcn.vmtu.core.util;

import top.vmctcn.vmtu.core.VMTUCore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AssetUtil {
    private static final String ASSET_ROOT = "https://gitee.com/Wulian233/vmtu/raw/main/resourcepack/";

    public static void download(String url, Path localFile) throws IOException, URISyntaxException {
        VMTUCore.LOGGER.info("Downloading: {} -> {}", url, localFile);
        FileUtils.copyURLToFile(new URI(url).toURL(), localFile.toFile(),
                (int) TimeUnit.SECONDS.toMillis(3), (int) TimeUnit.SECONDS.toMillis(33));
        VMTUCore.LOGGER.debug("Downloaded: {} -> {}", url, localFile);
    }

    public static String getString(String url) throws IOException, URISyntaxException {
        return IOUtils.toString(new URI(url).toURL(), StandardCharsets.UTF_8);
    }

    public static String getFastestUrl() {
        List<String> urls = new ArrayList<>();
        urls.add(ASSET_ROOT);

        ExecutorService executor = Executors.newFixedThreadPool(Math.max(urls.size(), 10));
        try {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String url : urls) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return testUrlConnection(url);
                    } catch (IOException e) {
                        return null; // 表示失败
                    } catch (URISyntaxException e) {
                        return null;
                    }
                }, executor);
                futures.add(future);
            }

            // 阻塞等待最快完成且成功的任务
            String fastest = null;
            while (!futures.isEmpty()) {
                CompletableFuture<Object> first = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]));
                fastest = (String) first.join();

                // 移除已完成的 future
                futures.removeIf(CompletableFuture::isDone);

                if (fastest != null) {
                    // 成功，取消其他任务
                    for (CompletableFuture<String> f : futures) {
                        f.cancel(true);
                    }
                    VMTUCore.LOGGER.info("Using fastest url: {}", fastest);
                    return fastest;
                }
            }

            // 全部失败，返回默认 URL
            VMTUCore.LOGGER.info("All urls are unreachable, using ASSET_ROOT");
            return ASSET_ROOT;

        } finally {
            executor.shutdownNow();
        }
    }

    private static String testUrlConnection(String url) throws IOException, URISyntaxException {
        HttpURLConnection conn = (HttpURLConnection) new URI(url).toURL().openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(5000);
        conn.connect();
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            return url;
        }
        VMTUCore.LOGGER.debug("URL unreachable: {}, code: {}", url, code);
        throw new IOException("URL unreachable: " + url);
    }
}
