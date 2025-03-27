package com.achiko.backend.util;

import java.io.File;
import java.io.IOException;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

/**
 * Scrimage 기반 WebP 변환 유틸리티
 */
public class WebPConverter {

    /**
     * Scrimage를 사용하여 WebP로 변환
     * @param inputFile  원본 이미지 파일
     * @param outputFile 변환된 WebP 파일
     * @throws IOException 파일 처리 중 예외 발생 시
     */
    public static void convertToWebP(File inputFile, File outputFile) throws IOException {
        // 1) Scrimage ImmutableImage로 로드
        ImmutableImage image = ImmutableImage.loader().fromFile(inputFile);
        
        // 2) WebP 포맷으로 저장 (기본: 무손실이 아닌 default 설정)
        image.output(WebpWriter.DEFAULT, outputFile);
    }
}
