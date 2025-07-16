
import base64

with open("1944937117046149120_1752544118548.jpg", "rb") as f:
    b64 = base64.b64encode(f.read()).decode()

image_str = "data:image/jpeg;base64," + b64
# 写入文本文件，方便后续查看、使用
with open("image_base642.txt", "w") as f_out:
    f_out.write(image_str)