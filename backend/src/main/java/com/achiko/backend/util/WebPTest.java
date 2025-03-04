package com.achiko.backend.util;

import java.io.File;

/**
 * WebP 변환 테스트용 클래스
 * - Scrimage 기반 변환 로직 검증
 */
public class WebPTest {
    public static void main(String[] args) {
        try {
            // 1) 원본 이미지 파일
            File inputFile = new File("C:/test/original.jpg");

            // 2) 변환 결과(WebP) 저장 위치
            File outputFile = new File("C:/test/output.webp");

            // 3) WebP 변환
            WebPConverter.convertToWebP(inputFile, outputFile);

            System.out.println("WebP 변환 성공: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
