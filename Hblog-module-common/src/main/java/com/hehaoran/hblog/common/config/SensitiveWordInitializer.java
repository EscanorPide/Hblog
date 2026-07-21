package com.hehaoran.hblog.common.config;

import com.hehaoran.hblog.common.utils.SensitiveWordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 启动时加载 classpath:Vocabulary/*.txt 全部词库，构建敏感词 Trie
 */
@Component
@Order(1)
@Slf4j
public class SensitiveWordInitializer implements ApplicationRunner {

    /** 词库目录（resources/Vocabulary 下所有 .txt） */
    private static final String VOCAB_PATTERN = "classpath*:Vocabulary/*.txt";

    @Override
    public void run(ApplicationArguments args) {
        Set<String> words = loadAllVocabulary();
        SensitiveWordUtil.init(words);
        log.info("==> 敏感词 Trie 初始化完成，共加载 {} 个词", words.size());
    }

    private Set<String> loadAllVocabulary() {
        Set<String> words = new HashSet<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(VOCAB_PATTERN);
            if (resources.length == 0) {
                log.warn("==> 未找到词库文件: {}，Trie 将为空", VOCAB_PATTERN);
                return words;
            }

            for (Resource resource : resources) {
                int before = words.size();
                loadOneFile(resource, words);
                log.info("==> 已加载词库 [{}]，新增约 {} 词（累计 {}）",
                        resource.getFilename(),
                        Math.max(0, words.size() - before),
                        words.size());
            }
        } catch (Exception e) {
            log.error("==> 扫描词库目录失败: {}", VOCAB_PATTERN, e);
        }
        return words;
    }

    private void loadOneFile(Resource resource, Set<String> words) {
        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                // 跳过空行、注释、单字（单字误伤率极高，如「真」「日」）
                if (!StringUtils.hasText(word)
                        || word.startsWith("#")
                        || word.startsWith("//")
                        || word.length() < 2) {
                    continue;
                }
                words.add(word);
            }
        } catch (Exception e) {
            log.error("==> 加载词库文件失败: {}", resource.getFilename(), e);
        }
    }
}
