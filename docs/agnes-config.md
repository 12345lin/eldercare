# Spring AI - Agnes API 配置说明
# 
# 本项目使用 Agnes AI 作为 LLM 提供商（免费）
# 
# 配置方式：
# 1. 设置环境变量 AGNES_API_KEY
#    - Windows PowerShell: $env:AGNES_API_KEY="你的key"
#    - Windows CMD: set AGNES_API_KEY=你的key
#    - Linux/Mac: export AGNES_API_KEY='你的key'
# 
# 2. 或者在 IDEA 中配置：
#    Run/Debug Configurations → Environment variables
#    添加：AGNES_API_KEY=你的key
# 
# API 信息：
# - Base URL: https://apihub.agnes-ai.com/v1
# - Model: agnes-2.5-flash
# - 兼容性: OpenAI 兼容格式
# - 价格: 免费（当前）
# 
# 支持的模型列表：
# - agnes-2.0-flash
# - agnes-image-2.0-flash
# - agnes-2.5-flash        ← 当前使用（最快）
# - agnes-2.5-pro-alpha
# - agnes-image-2.1-flash
# - agnes-2.5-pro
# - agnes-video-v2.0
