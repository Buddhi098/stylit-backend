package com.stylit.online.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shop/chat")
@RequiredArgsConstructor
@Validated
public class ShopBotController {
}
