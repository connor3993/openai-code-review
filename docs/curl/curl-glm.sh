curl --request POST \
  --url https://open.bigmodel.cn/api/paas/v4/chat/completions \
  --header 'Authorization: Bearer 960792da84824b6aa1db76c6194fc655.BTyIcSmZtjYyFQlp' \
  --header 'Content-Type: application/json' \
  --data '
{
  "model": "glm-4.5-flash",
  "messages": [
    {
      "role": "system",
      "content": "你是编程助手，擅长写简洁高效的代码。"
    },
    {
      "role": "user",
      "content": "写一个 Python 函数，计算斐波那契数列第 n 项。"
    }
  ],
  "stream": false,
  "temperature": 1
}
'