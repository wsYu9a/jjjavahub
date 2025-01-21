# **1、企业资产信息收集**
<font style="color:rgb(51, 51, 51);"> 了解目标的组织结构，尤其是子公司、股权比例及与母公司关系，主要适用于大型集团、企业、政党单位和高校等复杂结构的收集。对于小型目标，由于其结构简单，通常不需要收集组织架构信息。  </font>

| <font style="color:rgb(51, 51, 51);">企业信息</font> | [天眼查-商业查询平台_企业信息查询_公司查询_工商查询_企业信用信息系统](https://www.tianyancha.com/) |
| --- | --- |
| <font style="color:rgb(51, 51, 51);">企业信息</font> | [获客营销系统_ai智能拓客系统_企业获客系统-小蓝本获客系统](https://www.xiaolanben.com/) |
| <font style="color:rgb(51, 51, 51);">企业信息</font> | [企查查 - 查企业_查老板_查风险_企业信息查询系统](https://www.qcc.com/) |
| <font style="color:rgb(51, 51, 51);">企业信息</font> | [https://aiqicha.baidu.com/](https://aiqicha.baidu.com/)<font style="color:rgb(51, 51, 51);"> 爱企查</font> |
| <font style="color:rgb(51, 51, 51);">企业信息</font> | [https://opencorporates.com/](https://opencorporates.com/)<font style="color:rgb(51, 51, 51);">  国外企查  </font> |
| <font style="color:rgb(51, 51, 51);">企业信息</font> | [启信宝-企业查询_企业信用信息平台](https://www.qixin.com/) |


## <font style="color:rgb(51, 51, 51);">股权收集法</font>
 通过**天眼查、爱企查等平台**查看股权穿透图，筛选股权占比大于50%的子公司并记录为 `company.txt`，从而了解目标在整体结构中的位置及与其他公司的关系，完成组织结构的收集。对于股权占比小于50%的子公司，通常不需要关注，因为它们与目标的关联较弱。  

## <font style="color:rgb(51, 51, 51);">关键人物收集法</font>
 通过查找公司的法人或高管等关键人物，进一步挖掘其代表的公司或子公司信息。这个方法与股权收集法互补，帮助收集更多子公司信息，最终将股权占比较大的子公司名称记录并保存为 `company.txt`。  

# **2、域名信息收集**
## 主域名信息收集
主域名信息收集通过利用从组织结构收集到的子公司名（如 `company.txt`），进一步收集每个子公司的主域名。这一过程环环相扣，只有组织结构收集越全面，才能获得更多主域名，从而扩大攻击面，提高成功的可能性。  

| 备案信息 | [https://beian.miit.gov.cn/](https://beian.miit.gov.cn/)  备案管理系统   |
| --- | --- |
| <font style="color:rgb(51, 51, 51);">第三方程序查询接口</font> | [ICP备案查询网 - 网站备案查询 - 工信部域名备案查询实时数据](https://www.beianx.cn/) |
| 小蓝本 | [小蓝本-商业信息搜索](https://sou.xiaolanben.com/) |


### <font style="color:rgb(51, 51, 51);">ICP备案</font>
#### <font style="color:rgb(51, 51, 51);">官网查询接口</font>
官网ICP备案查询接口可以通过公司名查询其主域名，适用于子公司较少的小型目标。由于该接口存在图形汉字验证码，批量提取较为困难，因此更适合手动操作。  

#### <font style="color:rgb(51, 51, 51);">第三方程序查询接口</font>
 第三方程序查询接口，如 `icpsearch` 工具，适用于大型目标，可以批量提取所有子公司主域名，命令为 `icpsearch -f company.txt`。该方法没有验证码限制，但缺点是更新速度不如官网查询接口。

[GitHub - A10ha/ICPSearch: ICP 备案批量查询工具](https://github.com/A10ha/ICPSearch)

提取后的结果可以使用 Sublime Text 通过正则表达式 `([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}` 进行筛选，快速复制出域名。  

[Sublime Text - the sophisticated text editor for code, markup and prose](https://www.sublimetext.com/)

### 小蓝本
<font style="color:rgb(51, 51, 51);"> 使用小蓝本可以快速收集主域名，虽然不能一次性查出所有子公司主域名，但可以通过编写脚本来批量爬取。在知识产权页面上，一键获取ICP备案后，手动复制数据，使用 Sublime Text 正则表达式提取出主域名。  </font>

<font style="color:rgb(51, 51, 51);"></font>

<font style="color:rgb(51, 51, 51);">到此就通过</font>**<font style="color:rgb(51, 51, 51);">company.txt</font>**<font style="color:rgb(51, 51, 51);">收集到了很多的子公司的主域名，保存为</font>**<font style="color:rgb(51, 51, 51);">domain.txt</font>**

## 子域名信息收集
### 网络空间引擎查询
| FOFA | [https://fofa.info/](https://get-shell.com/?golink=aHR0cHM6Ly9mb2ZhLmluZm8v) |
| --- | --- |
| 全球鹰 | [http://hunter.qianxin.com/](https://get-shell.com/?golink=aHR0cDovL2h1bnRlci5xaWFueGluLmNvbS8=) |
| 360 | [https://quake.360.cn/quake/](https://get-shell.com/?golink=aHR0cHM6Ly9xdWFrZS4zNjAuY24vcXVha2UvIy9pbmRleA==) |
| 钟馗之眼 | [https://www.zoomeye.org/](https://get-shell.com/?golink=aHR0cHM6Ly93d3cuem9vbWV5ZS5vcmcvP1Ixbkc=) |
| 零零信安 | [https://0.zone/](https://get-shell.com/?golink=aHR0cHM6Ly8wLnpvbmUv) |
| Shodan | [https://www.shodan.io/](https://get-shell.com/?golink=aHR0cHM6Ly93d3cuc2hvZGFuLmlvLw==) |
| Censys | [https://censys.io/](https://get-shell.com/?golink=aHR0cHM6Ly9jZW5zeXMuaW8v) |
| ONYPHE | [https://www.onyphe.io/](https://get-shell.com/?golink=aHR0cHM6Ly93d3cub255cGhlLmlvLw==) |
| FullHunt | [https://fullhunt.io/](https://get-shell.com/?golink=aHR0cHM6Ly9mdWxsaHVudC5pby8=) |
| Soall Search Engine | [https://soall.org/](https://get-shell.com/?golink=aHR0cHM6Ly9zb2FsbC5vcmcv) |
| Netlas | [https://app.netlas.io/responses/](https://get-shell.com/?golink=aHR0cHM6Ly9hcHAubmV0bGFzLmlvL3Jlc3BvbnNlcy8=) |
| Leakix | [https://leakix.net/](https://get-shell.com/?golink=aHR0cHM6Ly9sZWFraXgubmV0Lw==) |
| DorkSearch | [https://dorksearch.com/](https://get-shell.com/?golink=aHR0cHM6Ly9kb3Jrc2VhcmNoLmNvbS8=) |


#### <font style="color:rgb(51, 51, 51);">举例：fofa</font>
```plain
domain ="主域名" || cert="公用名" || cert="组织名1" ||  || cert="组织名2" || cert="组织名3" ......
```

<font style="color:rgb(51, 51, 51);">domain是通过域名查，能查到web资产与非web资产，cert是通过证书查，只能查web资产，二者使用" || "结合拼接，会得到更多的子域名资产</font>

+ **domain 查询**<font style="color:rgb(51, 51, 51);">：通过主域名查询子域名和非 Web 资产。</font>

```plain
domain="主域名"
```

+ **cert 查询**<font style="color:rgb(51, 51, 51);">：通过证书中的公用名和组织名来查询 Web 资产。</font>

```plain
cert="公用名" || cert="组织名"
```

**公用名通常是网站的主域名或子域名，而同一个主域名可能会有多个组织名。因此，要尽可能多地找到与目标相关的公用名和组织名，可以通过手动翻查 https 站点的证书来获取这些信息。  **

![image-1735206016686](./assets/image-1735206016686.png)

![image-1735206016929](./assets/image-1735206016929.png)

![image-1735206017120](./assets/image-1735206017120.png)

+ **综合查询**<font style="color:rgb(51, 51, 51);">：结合 </font>`<font style="color:rgb(51, 51, 51);">domain</font>`<font style="color:rgb(51, 51, 51);"> 和 </font>`<font style="color:rgb(51, 51, 51);">cert</font>`<font style="color:rgb(51, 51, 51);">，通过 </font>`<font style="color:rgb(51, 51, 51);">||</font>`<font style="color:rgb(51, 51, 51);"> 拼接，可以查询更多资产，特别是隐蔽资产。例如：</font>

```plain
domain="huaweiyun.com" || cert="huaweiyun.com" || cert="huaweicloud.com" || cert="Huawei Software Technologies Co., Ltd."
```

<font style="color:rgb(51, 51, 51);">大量资产不便测试，使用证书查询可收集更多资产、扩大攻击面，但需根据实际情况灵活调整。对于像华为这样的大企业，资产已很多，不适合使用证书查询；更适合中小型企业、政府和高校等资产较少的单位。</font>

<font style="color:rgb(51, 51, 51);"> 为了批量查询 </font>`<font style="color:rgb(51, 51, 51);">domain.txt</font>`<font style="color:rgb(51, 51, 51);"> 中的所有子域名，写了一个脚本，将每个主域名和公用名拼接成 </font>`<font style="color:rgb(51, 51, 51);">domain="主域名" || cert="公用名" || ...</font>`<font style="color:rgb(51, 51, 51);"> 格式，再丢到 Fofa 查询。这样拼接能通过 </font>`<font style="color:rgb(51, 51, 51);">||</font>`<font style="color:rgb(51, 51, 51);"> 查到更多资产。</font>

##### <font style="color:rgb(51, 51, 51);">脚本-join.py</font>
```plain
python join.py
```

```plain
# 读取 domain.txt 文件并拼接数据
def read_and_concatenate(file_path):
    try:
        with open(file_path, 'r') as file:
            # 读取文件中的每一行，去掉空格和换行符
            lines = [line.strip() for line in file if line.strip()]

        # 使用 ' || ' 连接每行数据
        result = ' || '.join([f'domain="{line}" || cert="{line}"' for line in lines])

        print(result)
    except FileNotFoundError:
        print(f"文件 {file_path} 未找到。")
    except Exception as e:
        print(f"发生错误: {e}")

# 调用函数并传入文件路径
read_and_concatenate('domain.txt')
```

接下来手动查找 HTTPS 站点的证书，结合“**英文证书查询**”方法，手动复制公用名和组织名，并将其拼接到 `cert` 查询语法中。由于大量资产的公用名和组织名一致，这种方式能快速扩展查询范围。  

#### 格式处理
为了处理 Fofa 导出结果中的格式混乱问题，可以通过脚本将不同类型的资产分离。这样，纯域名可以用于 C 段整理，纯 IP 用于端口扫描，带协议头的 URL 用于活跃测试、爬取 JS 或漏洞扫描等。通过脚本自动化这个过程，可以大大提高工作效率。  

##### <font style="color:rgb(51, 51, 51);">脚本-format.py</font>
 输入 `input.txt` 中格式混乱的 URL，输出 `subdomain-1.txt`（子域名）和 `url-1.txt`（URL）。IP 列不需要分离，FOFA 已能导出格式整齐的 IP 列。 

```plain
python format.py
```

```plain
import re
import chardet

# 自动检测文件编码
def detect_encoding(filename):
    with open(filename, 'rb') as file:
        result = chardet.detect(file.read())
    return result['encoding']

# 读取文件并处理内容
def process_file(input_file, subdomain_file, url_file):
    encoding = detect_encoding(input_file)

    # 打开input.txt文件并读取每一行
    with open(input_file, 'r', encoding=encoding) as file:
        lines = file.readlines()

    # 准备两个列表来存储不同的输出内容
    subdomains = []
    urls = []

    # 定义一个正则表达式来匹配特定的IP地址URL
    specific_ip_url_pattern = re.compile(r'http[s]?://127\.0\.0\.1(:\d+)?/?')

    # 处理每一行数据
    for line in lines:
        # 去除两端的空格和换行符
        line = line.strip()

        # 检查URL是否是特定的IP地址URL
        if specific_ip_url_pattern.match(line):
            # 如果是，则直接写入url.txt
            urls.append(line)
        else:
            # 检查URL是否基于IP地址，如果是则跳过
            if re.match(r'http[s]?://\d{1,3}(\.\d{1,3}){3}(:\d+)?/?', line):
                continue  # 如果当前行是基于IP的URL，跳过不处理

            # 检查并处理每种情况
            if line.startswith('http://'):
                subdomains.append(line[7:])  # 从'http://'后面开始截取
                urls.append(line)  # 保持原样
            elif line.startswith('https://'):
                subdomains.append(line[8:])  # 从'https://'后面开始截取
                urls.append(line)  # 保持原样
            else:
                subdomains.append(line)  # 没有协议头，直接使用
                urls.append('http://' + line)  # 添加'http://'
                urls.append('https://' + line) # 添加"https://" 

    # 将处理后的数据写入相应的文件
    with open(subdomain_file, 'w', encoding=encoding) as file:
        for subdomain in subdomains:
            file.write(subdomain + '\n')

    with open(url_file, 'w', encoding=encoding) as file:
        for url in urls:
            file.write(url + '\n')

# 调用处理函数
process_file('input.txt', 'subdomain.txt', 'url.txt')
```

 整理 `url.txt` 时，最好为每个域名添加 `http` 和 `https`，确保资产更全。对于只需 `http` 或 `https` 的，可以在脚本中注释调整。  

```plain
# 检查并处理每种情况
            if line.startswith('http://'):
                subdomains.append(line[7:])  # 从'http://'后面开始截取
                urls.append(line)  # 保持原样
            elif line.startswith('https://'):
                subdomains.append(line[8:])  # 从'https://'后面开始截取
                urls.append(line)  # 保持原样
            else:
                subdomains.append(line)  # 没有协议头，直接使用
                urls.append('http://' + line)  # 添加'http://'
                #urls.append('https://' + line) # 添加"https://"
```

<font style="color:rgb(51, 51, 51);"> Fofa 导出的 IP 格式规范，无需处理，直接复制保存为 </font>`<font style="color:rgb(51, 51, 51);">ip-1.txt</font>`

到此，我们通过 `domain.txt` 获取了 `subdomain-1.txt`、`url-1.txt` 和 `ip-1.txt`，并保存备用，后续会进行合并去重处理。

### **英文证书查询**
<font style="color:rgb(51, 51, 51);"> 除了 Fofa 中的证书查询，还可以通过使用 crt.sh 来查询证书为英文名的资产，补充 Fofa 的结果。这种方法能够找到许多隐蔽的资产，并获得大量的公用名和组织名，之后可以将这些信息拼接到查询语法中，进一步扩大资产收集的范围。  </font>

[crt.sh | Certificate Search](https://crt.sh/)

#### <font style="color:rgb(51, 51, 51);">查询方式</font>
##### <font style="color:rgb(51, 51, 51);">q模式（比较精准的模式）</font>
```plain
?q=huawei.com
```

##### <font style="color:rgb(51, 51, 51);">o模式（模糊查询模式，o模式有些情况下查不出来）</font>
```plain
?o=huawei
```

<font style="color:rgb(51, 51, 51);"> 为了批量提取 crt.sh 中的 common name（公用名），可以编写脚本利用正则表达式进行提取。由于公用名大多数是域名或子域名，这种方式能够帮助发现许多隐蔽资产，广泛应用于漏洞挖掘，尤其是在 SRC 漏洞挖掘中。  </font>

##### <font style="color:rgb(51, 51, 51);">脚本-crt.sh.py</font>
```plain
python crt.sh.py
```

```plain
import re
import requests

def extract_unique_common_names(url):
    try:
        # 自定义 User-Agent 头
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        }

        # 获取页面内容
        response = requests.get(url, headers=headers)
        response.raise_for_status()  # 检查是否请求成功
        html_content = response.text

        # 提取 <TD> 标签中以 .cn, .com, .org, .net 结尾的内容
        common_names = re.findall(r'<TD.*?>([^<]*\.(?:cn|com|org|net))</TD>', html_content, re.S)

        # 去重处理
        unique_common_names = set(common_names)

        # 输出提取的公用名
        if unique_common_names:
            print("Extracted Unique Common Names:")
            for name in unique_common_names:
                print(name.strip())  # 去掉前后的空格
        else:
            print("No Common Names found.")
    except requests.RequestException as e:
        print(f"Error fetching URL: {e}")

# 调用函数并传入 URL
url = "https://crt.sh/?q=baidu.com"  # 替换为实际的查询目标和模式
extract_unique_common_names(url)
```

#### 格式处理
按照相同方法，把这些域名复制下来，使用刚才的 `format.py` 脚本生成带协议头的 URL 资产和不带协议头的子域名资产。到此，我们得到了 `subdomain-2.txt` 和 `url-2.txt`。

### OneForAll
集成了多种方法，包括 crt.sh 查找、VirusTotal 查找、搜索引擎语法、各种测绘引擎、API 接口、子域名爆破、DNS 域传送、子域名接管、Fuzzing 等等。通过正确配置有效提升子域名收集的效率和准确性:

1. **配置 API**：去 `config` 目录，配置文件中的 API 接口。某些 API 可能是收费的，可以选择不配置。
2. **启用模块**：根据需要启用不同的模块，比如 DNS 查询、暴力破解、外部服务查询等。这样 OneForAll 就能全面发挥作用，收集到更多的子域名信息。

[GitHub - shmilylty/OneForAll: OneForAll是一款功能强大的子域收集工具](https://github.com/shmilylty/OneForAll)

```plain
python oneforall.py --target huaweiyun.com run
python oneforall.py --targets domain.txt run
```

#### <font style="color:rgb(51, 51, 51);">格式处理</font>
以 `huaweiyun.com` 为单个目标演示，实际是对 `domain.txt` 中的主域名批量收集子域名。如果主域名太多，也可以自由选择处理。OneForAll 的结果较为规范，因此无需再进行额外处理。

到此，我们通过 `domain.txt` 获取了 `subdomain-3.txt`、`url-2.txt` 和 `ip-2.txt`。

#### <font style="color:rgb(51, 51, 51);">去重</font>
接下来，将以下文件进行合并：

+ `subdomain-1.txt`、`subdomain-2.txt`、`subdomain-3.txt` 合并为 `subdomain.txt`
+ `url-1.txt`、`url-2.txt`、`url-3.txt` 合并为 `url.txt`
+ `ip-1.txt`、`ip-2.txt` 合并为 `ip.txt`

然后，进行去重处理。

[在线文本去重复分隔工具_蛙蛙工具](https://www.iamwawa.cn/quchong.html)

这样，就完成了子域名、URL 和 IP 资产的收集与整理，为接下来的信息收集做好了准备。

# **3、**端口信息收集
### gogo
[GitHub - chainreactors/gogo: 面向红队的, 高度可控可拓展的自动化引擎](https://github.com/chainreactors/gogo)

### fscan
[GitHub - shadow1ng/fscan: 一款内网综合扫描工具，方便一键自动化、全方位漏扫扫描。](https://github.com/shadow1ng/fscan)

<font style="color:rgb(51, 51, 51);">收集了大量主域名、子域名和初步资产后，为了发现更多隐蔽资产，需要对服务器进行全端口扫描，特别是一些目标会在同一服务器上开放多个端口。端口扫描可以帮助扩大攻击面，因此在整理好 </font>`<font style="color:rgb(51, 51, 51);">ip.txt</font>`<font style="color:rgb(51, 51, 51);"> 后，使用fscan进行全端口扫描，同时进行漏洞扫描和弱口令爆破。  </font>

```plain
fscan.exe -hf ip.txt -t 3000 -p 1-65535 -num 100 -np -o result.txt
```

[GitHub - ZororoZ/fscanOutput: 一个用于处理fsacn输出结果的小脚本（尤其面对大量资产的fscan扫描结果做输出优化，让你打点快人一步！！！）](https://github.com/ZororoZ/fscanOutput)

由于端口扫描时间较长，建议将其放在国外的VPS上后台运行。扫描完成后，fscan 的输出格式较为混乱，不便于整理，可以使用 `fscanoutput.py` 脚本对扫描结果进行归类和整理。  

```plain
python fscanOutput.py result.txt
```

# <font style="color:rgb(51, 51, 51);">4、C段收集</font>
**收集 C 段是非常重要的，尤其对于大型目标**，因为他们通常会按 C 段购买 IP 资产（如 220.111.222.1/24），整个 C 段可能都属于一个目标。此外，一些 IP 资产可能没有证书或域名，常规收集方法难以发现。通过扫描目标的 C 段，可以探测存活的 IP 和开放的端口，进而发现一些隐蔽的资产，扩大攻击面。

**对于小型目标，收集 C 段不推荐**，因为同一 C 段内属于该目标的 IP 资产较少，容易造成误差。  

### Eeyes
[GitHub - EdgeSecurityTeam/Eeyes: Eeyes(棱眼)-快速筛选真实IP并整理为C段](https://github.com/EdgeSecurityTeam/Eeyes)

 收集目标的 C 段推荐使用 **Eeyes** 工具，它可以根据目标的域名列表整理出目标的 C 段。此时，我们可以使用前面整理好的 `subdomain.txt` 文件作为输入。Eeyes 会优先排除那些架设了 CDN 的资产，然后再对剩余的 IP 进行 C 段整理，确保收集到的 C 段与实际目标关联。 

```plain
Eeyes -l subdomain.txt
```

<font style="color:rgb(51, 51, 51);">根据整理结果手动选择几个存活ip比较多的C段，保存为c.txt</font> 

拿到 C 段后，下一步是端口扫描。将 C 段与 `ip.txt` 合并为 `ip_c.txt`，然后交给 fscan 进行全端口扫描。

```plain
fscan.exe -hf ip_c.txt -t 3000 -p 1-65535 -num 100 -np -o result.txt
```

有些资产严格绑定域名，如 `test.com:8080` 对应的 IP `111.222.333.444:8080` 可能无法直接访问。因此，建议将 `subdomain.txt` 也加入，进行全端口扫描，以确保不漏掉任何资产。  

```plain
fscan.exe -hf ip_c_subdomain.txt -t 3000 -p 1-65535 -num 100 -np -o result.txt
```

 扫描完成后，将发现的更多 Web 资产合并到 `url.txt` 中，确保资产更全。  

# <font style="color:rgb(51, 51, 51);">5、HOST碰撞</font>
通过 HOST 碰撞进一步收集隐蔽资产。将 `subdomain.txt` 批量解析域名，记录无法解析的域名作为待碰撞字典。`ip_c.txt` IP 不多，可以全部碰撞或手动筛选。

### <font style="color:rgb(51, 51, 51);">HostCollision</font>  
[GitHub - pmiaowu/HostCollision: 用于host碰撞而生的小工具,专门检测渗透中需要绑定hosts才能访问的主机或内部系统](https://github.com/pmiaowu/HostCollision)

### <font style="color:rgb(51, 51, 51);">Hosts_scan</font>
[GitHub - fofapro/Hosts_scan: 这是一个用于IP和域名碰撞匹配访问的小工具，旨意用来匹配出渗透过程中需要绑定hosts才能访问的弱主机或内部系统。](https://github.com/fofapro/Hosts_scan)

### <font style="color:rgb(51, 51, 51);">脚本-domain_auth.py</font>
```plain
import socket

def check_domain_resolution(domain):
    try:
        # 尝试获取域名对应的IP地址
        ip = socket.gethostbyname(domain)
        return True
    except socket.gaierror:
        # 如果发生解析错误，说明该域名无法解析
        return False

def main():
    # 读取子域名文件
    input_file = 'subdomain.txt'
    output_file = 'result.txt'

    with open(input_file, 'r') as file:
        domains = [line.strip() for line in file.readlines()]

    # 检查每个域名的解析情况
    unresolved_domains = []
    for domain in domains:
        if not check_domain_resolution(domain):
            print(f"无法解析的域名: {domain}")
            unresolved_domains.append(domain)

    # 将无法解析的域名写入到result.txt
    if unresolved_domains:
        with open(output_file, 'w') as result_file:
            for domain in unresolved_domains:
                result_file.write(f"{domain}\n")
        print(f"解析异常的域名已写入 {output_file}")
    else:
        print("所有域名均能解析")

if __name__ == '__main__':
    main()
```

<font style="color:rgb(51, 51, 51);">经过HOST碰撞后，运气好的话直接就撕开口子了，可能发现一些敏感脆弱的资产。</font>

# 6、指纹识别
<font style="color:rgb(51, 51, 51);">目前已经得到了经过了整合的url.txt，为了更全一点，把之前整理的ip.txt也加入进去，加入之前，先给每一个ip加上http协议头</font>

### <font style="color:rgb(51, 51, 51);">脚本ipadd.py</font>
```plain
# 打开输入文件
with open("input.txt", "r") as input_file:
    # 读取文件内容并按行分割
    ip_addresses = input_file.read().splitlines()

# 打开输出文件
with open("output.txt", "w") as output_file:
    # 遍历每个 IP 地址
    for ip in ip_addresses:
        # 添加 http:// 前缀
        http_ip = "http://" + ip
        # 添加 https:// 前缀
        https_ip = "https://" + ip
        # 将结果写入输出文件
        output_file.write(http_ip + "\n")
        output_file.write(https_ip + "\n")
```

```plain
python ipadd.py
```

<font style="color:rgb(51, 51, 51);">然后再次对url.txt进行去重，就得到了</font>`<font style="color:rgb(51, 51, 51);">web.txt</font>`**<font style="color:rgb(51, 51, 51);">（经过了多次整理得到的最终的web总资产）</font>**

<font style="color:rgb(51, 51, 51);">下一步是存活探测，使用指纹识别工具进行探测。指纹识别不仅能确认存活，还能帮助我们优先发现漏洞多的 CMS 或 OA 系统，快速切入 Nday 漏洞。  </font>

**<font style="color:rgb(51, 51, 51);">使用TideFInger或者Ehole做指纹识别</font>**

### **<font style="color:rgb(51, 51, 51);"></font>**<font style="color:rgb(51, 51, 51);">TideFInger</font>
[GitHub - TideSec/TideFinger_Go: 一个Go版(更强大)的TideFinger指纹识别工具，可对web和主机指纹进行识别探测，整合梳理互联网指纹2.3W余条，在效率和指纹覆盖面方面进行了平衡和优化。](https://github.com/TideSec/TideFinger_Go)

```plain
TideFinger -uf web.txt -nobr -nopoc 
TideFinger -uf web.txt
```

### 棱洞
[GitHub - EdgeSecurityTeam/EHole: EHole(棱洞)3.0 重构版-红队重点攻击系统指纹探测工具](https://github.com/EdgeSecurityTeam/EHole)

```plain
ehole.exe finger -l web.txt
```

### 棱洞魔改
[GitHub - lemonlove7/EHole_magic: EHole(棱洞)魔改。可对路径进行指纹识别；支持识别出来的重点资产进行漏洞检测(支持从hunter和fofa中提取资产)支持对ftp服务识别及爆破](https://github.com/lemonlove7/EHole_magic)

### 无影
[GitHub - TideSec/TscanPlus: 一款综合性网络安全检测和运维工具，旨在快速资产发现、识别、检测，构建基础资产信息库，协助甲方安全团队或者安全运维人员有效侦察和检索资产，发现存在的薄弱点和攻击面。](https://github.com/TideSec/TscanPlus)

# 7、自动化信息收集（懒人直接选择）
### ENScan_GO
[GitHub - wgpsec/ENScan_GO: 一款基于各大企业信息API的工具，解决在遇到的各种针对国内企业信息收集难题。一键收集控股公司ICP备案、APP、小程序、微信公众号等信息聚合导出。](https://github.com/wgpsec/ENScan_GO)

### 灯塔
[GitHub - Aabyss-Team/ARL: ARL官方仓库备份项目：ARL(Asset Reconnaissance Lighthouse)资产侦察灯塔系统旨在快速侦察与目标关联的互联网资产，构建基础资产信息库。 协助甲方安全团队或者渗透测试人员有效侦察和检索资产，发现存在的薄弱点和攻击面。](https://github.com/Aabyss-Team/ARL)

### 灯塔魔改
[GitHub - ki9mu/ARL-plus-docker: 基于ARL-V2.6.2修改后的版本](https://github.com/ki9mu/ARL-plus-docker)

# 8、自动化漏扫
### Nuclei
[GitHub - projectdiscovery/nuclei: Nuclei is a fast, customizable vulnerability scanner powered by the global security community and built on a simple YAML-based DSL, enabling collaboration to tackle trending vulnerabilities on the internet. It helps you find vulnerabilities in your applications, APIs, networks, DNS, and cloud configurations.](https://github.com/projectdiscovery/nuclei) 

```plain
nuclei.exe -list test.txt -o output.txt
```

### Afrog
[GitHub - zan8in/afrog: A Security Tool for Bug Bounty, Pentest and Red Teaming.](https://github.com/zan8in/afrog) 

```plain
afrog -T urls.txt
```

### EZ
[GitHub - m-sec-org/EZ: EZ是一款集信息收集、端口扫描、服务暴破、URL爬虫、指纹识别、被动扫描为一体的跨平台漏洞扫描器。](https://github.com/m-sec-org/EZ)

```plain
./ez webscan --pocs sqli -u "http://www.example.com"
```

### Xray
[GitHub - chaitin/xray: 一款完善的安全评估工具，支持常见 web 安全问题扫描和自定义 poc | 使用之前务必先阅读文档](https://github.com/chaitin/xray)

```plain
#批量添加目标
python xray.py -r test.txt

# 爬取单个目标
xray.exe webscan --basic-crawler http://xxxx.com/ --html-output  output-a.html

# 监听7777端口，与Brup联动
xray.exe webscan --listen 127.0.0.1:7777 --html-output output-b.html
```

# 9、网站信息收集
## 网站基本信息
### <font style="color:rgb(51, 51, 51);">语言</font>
+ <font style="color:rgb(51, 51, 51);">可以使用</font><font style="color:rgb(51, 51, 51);"> </font>**<font style="color:rgb(51, 51, 51);">Wappalyzer</font>**<font style="color:rgb(51, 51, 51);"> </font><font style="color:rgb(51, 51, 51);">工具分析网站所使用的编程语言。</font>

### <font style="color:rgb(51, 51, 51);">数据库</font>
+ <font style="color:rgb(51, 51, 51);">使用</font><font style="color:rgb(51, 51, 51);"> </font>**<font style="color:rgb(51, 51, 51);">Wappalyzer</font>**<font style="color:rgb(51, 51, 51);"> </font><font style="color:rgb(51, 51, 51);">或</font><font style="color:rgb(51, 51, 51);"> </font>**<font style="color:rgb(51, 51, 51);">fofa</font>**<font style="color:rgb(51, 51, 51);"> </font><font style="color:rgb(51, 51, 51);">进行数据库类型的识别。</font>

### <font style="color:rgb(51, 51, 51);">web容器</font>
+ <font style="color:rgb(51, 51, 51);">同样，</font>**<font style="color:rgb(51, 51, 51);">Wappalyzer</font>**<font style="color:rgb(51, 51, 51);"> </font><font style="color:rgb(51, 51, 51);">和</font><font style="color:rgb(51, 51, 51);"> </font>**<font style="color:rgb(51, 51, 51);">fofa</font>**<font style="color:rgb(51, 51, 51);"> </font><font style="color:rgb(51, 51, 51);">可以帮助识别网站使用的Web容器（如Apache、Nginx等）。</font>

### <font style="color:rgb(51, 51, 51);">操作系统</font>
+ <font style="color:rgb(51, 51, 51);">可以通过对目标服务器进行大小写测试和Ping TTL值分析，间接推测目标服务器使用的操作系统。</font>

## 网站端口服务
### Nmap
[Download the Free Nmap Security Scanner for Linux/Mac/Windows](https://nmap.org/download.html)

### masscan
[GitHub - robertdavidgraham/masscan: TCP port scanner, spews SYN packets asynchronously, scanning entire Internet in under 5 minutes.](https://github.com/robertdavidgraham/masscan)

### railgun（推荐）
[GitHub - lz520520/railgun](https://github.com/lz520520/railgun)

## 目录
### dirsearch
[GitHub - maurosoria/dirsearch: Web path scanner](https://github.com/maurosoria/dirsearch)

### ffuf(推荐)
[GitHub - ffuf/ffuf: Fast web fuzzer written in Go](https://github.com/ffuf/ffuf)

## JS接口
### <font style="color:rgb(51, 51, 51);">downloadjs.py</font>
<font style="color:rgb(51, 51, 51);">本地浏览器控制台一键下载网站js文件的脚本</font>

```plain
// Function to download a file
function downloadFile(filename, content) {
    const blob = new Blob([content], { type: 'application/javascript' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// Function to fetch external JS file and download it
async function fetchAndDownload(url, defaultFilename) {
    try {
        const response = await fetch(url);
        const content = await response.text();
        const filename = url.split('/').pop() || defaultFilename;
        downloadFile(filename, content);
        console.log(`Downloaded external JS file: ${filename}`);
    } catch (error) {
        console.error(`Failed to download ${url}: ${error}`);
    }
}

// Function to download all JavaScript code found in the document
async function downloadAllJS() {
    // Collect all <script> elements in the document
    const scripts = Array.from(document.querySelectorAll('script'));
    let inlineCounter = 1;

    for (const script of scripts) {
        if (script.src) {
            // Case 2: External JS file
            await fetchAndDownload(script.src, `external-script-${inlineCounter}.js`);
        } else if (script.textContent) {
            // Case 1 & 3: Inline JS code (could include Webpack bundled code)
            const filename = `inline-script-${inlineCounter}.js`;
            downloadFile(filename, script.textContent);
            console.log(`Downloaded inline JS as: ${filename}`);
            inlineCounter++;
        }
    }
}

// Run the function to download all JS files
downloadAllJS();
```

### <font style="color:rgb(51, 51, 51);">findapi.py</font>
<font style="color:rgb(51, 51, 51);">提取js接口的脚本</font>

```plain
import json
import re
import requests
import sys
import os

headers = {
    "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"}

fileurl = sys.argv[1]

# 根据传入的文件名确定目录名
filemkdir = fileurl.split('_')[0]

# 确保目录存在
if not os.path.exists(filemkdir):
    os.makedirs(filemkdir)

# 获取路径
paths = []
for dirpath, dirnames, filenames in os.walk(filemkdir):
    for file in filenames:
        file_path = os.path.join(dirpath, file)
        try:
            with open(file_path, "r", encoding='gb18030', errors='ignore') as f2:
                for line in f2:
                    line = line.strip('\n').strip('\t')
                    p = re.findall(r'''(['"]\/[^][^>< \)\(\{\}]*?['"])''', line)
                    if p:
                        for path in p:
                            path = path.replace(':"', "").replace('"', "")
                            paths.append(file + "---" + path)
        except FileNotFoundError:
            print(f"File not found: {file_path}")
        except Exception as e:
            print(f"Error reading file {file_path}: {e}")

# 写入提取到的路径
output_file = fileurl + '_path.txt'
with open(output_file, "a+", encoding='gb18030', errors='ignore') as output:
    for var in sorted(set(paths)):
        output.write(var + '\n')
```

### **<font style="color:rgb(51, 51, 51);">JSfinder</font>**
[GitHub - Threezh1/JSFinder: JSFinder is a tool for quickly extracting URLs and subdomains from JS files on a website.](https://github.com/Threezh1/JSFinder)

### **<font style="color:rgb(51, 51, 51);">packer-fuzzer</font>**
[GitHub - rtcatc/Packer-Fuzzer: Packer Fuzzer is a fast and efficient scanner for security detection of websites constructed by javascript module bundler such as Webpack.](https://github.com/rtcatc/Packer-Fuzzer)

### **<font style="color:rgb(51, 51, 51);">URLfinder</font>**
[GitHub - pingc0y/URLFinder: 一款快速、全面、易用的页面信息提取工具，可快速发现和提取页面中的JS、URL和敏感信息。](https://github.com/pingc0y/URLFinder)

## 泄露
### <font style="color:rgb(51, 51, 51);">Github语法</font>
```plain
in:name test              #仓库标题搜索含有关键字
in:descripton test         #仓库描述搜索含有关键字
in:readme test             #Readme文件搜素含有关键字
stars:>3000 test           #stars数量大于3000的搜索关键字
stars:1000..3000 test      #stars数量大于1000小于3000的搜索关键字
forks:>1000 test           #forks数量大于1000的搜索关键字
forks:1000..3000 test      #forks数量大于1000小于3000的搜索关键字
size:>=5000 test           #指定仓库大于5000k(5M)的搜索关键字
pushed:>2019-02-12 test    #发布时间大于2019-02-12的搜索关键字
created:>2019-02-12 test   #创建时间大于2019-02-12的搜索关键字
user:test                  #用户名搜素
license:apache-2.0 test    #明确仓库的 LICENSE 搜索关键字
language:java test         #在java语言的代码中搜索关键字
user:test in:name test     #组合搜索,用户名test的标题含有test的

site:Github.com smtp
site:Github.com smtp @qq.com
site:Github.com smtp @126.com
site:Github.com smtp @163.com
site:Github.com smtp @sina.com.cn
site:Github.com smtp password
site:Github.com String password smtp
site:Github.com smtp @baidu.com

site:Github.com sa password
site:Github.com root password
site:Github.com User ID=’sa’;Password
site:Github.com inurl:sql

site:Github.com svn
site:Github.com svn username
site:Github.com svn password
site:Github.com svn username password

site:Github.com password
site:Github.com ftp ftppassword
site:Github.com 密码
site:Github.com 内部
```

#### <font style="color:rgb(51, 51, 51);">Github监控</font>
| <font style="color:rgb(51, 51, 51);">云绘监控</font> | [https://github.yhuisec.com/](https://github.yhuisec.com/) |
| :---: | :---: |


### <font style="color:rgb(51, 51, 51);">Githack</font>
[GitHub - lijiejie/GitHack: A `.git` folder disclosure exploit](https://github.com/lijiejie/GitHack)

### <font style="color:rgb(51, 51, 51);">Svnhack </font>
[GitHub - callmefeifei/SvnHack: 一个Svn信息泄露辅助工具，可以使用这个脚本列取网站目录，读取源码文件以及下载整站代码。](https://github.com/callmefeifei/SvnHack)

### <font style="color:rgb(51, 51, 51);">ds_store </font>
[GitHub - lijiejie/ds_store_exp: A .DS_Store file disclosure exploit. It parses .DS_Store file and downloads files recursively.](https://github.com/lijiejie/ds_store_exp)

## 旁站
[同IP网站查询,C段查询,IP反查域名,在线C段,旁站工具 - WebScan](https://www.webscan.cc/)

## <font style="color:rgb(51, 51, 51);">CMS识别（内容管理系统）</font>
<font style="color:rgb(51, 51, 51);">可以使用工具如云悉和360观星来识别目标网站是否使用了特定的CMS系统。通过识别CMS类型，可以帮助渗透测试人员找到已知的漏洞和攻击路径。</font>

| **<font style="color:rgb(51, 51, 51);">云悉</font>** | [https://www.yunsee.cn/](https://www.yunsee.cn/) |
| :---: | :---: |
| **<font style="color:rgb(51, 51, 51);">360观星</font>** | [https://fp.shuziguanxing.com/#/](https://fp.shuziguanxing.com/#/) |


# 10、社工信息收集
## tg社工机器人
配合互联网上泄露的目标人员信息使用，对目标的邮箱、微信等进行定点钓鱼

## 在线社工库
配合互联网上泄露的目标人员信息使用，对目标的邮箱、微信等进行定点钓鱼

## 谷歌黑客语法
```plain
PHP后缀参数
site:baidu.com ext:php inurl:?

openbugbounty关于该域名的报告
site:openbugbounty.org inurl:reports intext:"baidu.com"

敏感信息后缀
site:"baidu.com" ext:log | ext:txt | ext:conf | ext:cnf | ext:ini | ext:env | ext:sh | ext:bak | ext:backup | ext:swp | ext:old | ext:~ | ext:git | ext:svn | ext:htpasswd | ext:htaccess

易受XSS攻击的参数
inurl:q= | inurl:s= | inurl:search= | inurl:query= | inurl:keyword= | inurl:lang= inurl:& site:baidu.com

开放重定向参数
inurl:url= | inurl:return= | inurl:next= | inurl:redirect= | inurl:redir= | inurl:ret= | inurl:r2= | inurl:page= inurl:& inurl:http site:baidu.com

sql报错参数
inurl:id= | inurl:pid= | inurl:category= | inurl:cat= | inurl:action= | inurl:sid= | inurl:dir= inurl:& site:baidu.com

SSRF易发参数
inurl:http | inurl:url= | inurl:path= | inurl:dest= | inurl:html= | inurl:data= | inurl:domain= | inurl:page= inurl:& site:baidu.com

LFI常见参数
inurl:include | inurl:dir | inurl:detail= | inurl:file= | inurl:folder= | inurl:inc= | inurl:locate= | inurl:doc= | inurl:conf= inurl:& site:baidu.com

RCE常见参数
inurl:cmd | inurl:exec= | inurl:query= | inurl:code= | inurl:do= | inurl:run= | inurl:read= | inurl:ping= inurl:& site:baidu.com

敏感关键字
inurl:config | inurl:env | inurl:setting | inurl:backup | inurl:admin | inurl:php site:baidu.com

敏感参数
inurl:email= | inurl:phone= | inurl:password= | inurl:secret= inurl:& site:baidu.com

API文档
inurl:apidocs | inurl:api-docs | inurl:swagger | inurl:api-explorer site:"baidu.com"

代码泄露
site:pastebin.com "baidu.com"
site:jsfiddle.net "baidu.com"
site:codebeautify.org "baidu.com"
site:codepen.io "baidu.com"

云存储
site:s3.amazonaws.com "baidu.com"
site:blob.core.windows.net "baidu.com"
site:googleapis.com "baidu.com"
site:drive.google.com "baidu.com"
site:dev.azure.com "baidu.com"
site:onedrive.live.com "baidu.com"
site:digitaloceanspaces.com "baidu.com"
site:sharepoint.com "baidu.com"
site:s3-external-1.amazonaws.com "baidu.com"
site:s3.dualstack.us-east-1.amazonaws.com "baidu.com"
site:dropbox.com/s "baidu.com"
site:box.com/s "baidu.com"
site:docs.google.com inurl:"/d/" "baidu.com"

JFrog 软件工件管理工具
site:jfrog.io "baidu.com"

Firebase
site:firebaseio.com "baidu.com"

文件上传点
site:baidu.com ”choose file”

漏洞赏金计划和漏洞披露计划
"submit vulnerability report" | "powered by bugcrowd" | "powered by hackerone"
site:*/security.txt "bounty"

Apache服务器状态暴露
site:*/server-status apache

WordPress
inurl:/wp-admin/admin-ajax.php

Drupal
intext:"Powered by" & intext:Drupal & inurl:user

Joomla
site:*/joomla/login
```

# 11、代理池搭建
### ProxyCat
[GitHub - honmashironeko/ProxyCat: 一款部署于云端或本地的代理池中间件，可将静态代理IP灵活运用成隧道IP，提供固定请求地址，一次部署终身使用](https://github.com/honmashironeko/ProxyCat)

### befree
[GitHub - zidanfanshao/befree: 一款红队在信息收集时规避IP封禁的傻瓜式一键代理池，通过大量代理节点轮询的代理池工具](https://github.com/zidanfanshao/befree)

# 12、上传webshell
### 蚁剑
[GitHub - AntSwordProject/antSword: 中国蚁剑是一款跨平台的开源网站管理工具。AntSword is a cross-platform website management toolkit.](https://github.com/AntSwordProject/antSword)

### 冰蝎
[GitHub - rebeyond/Behinder: “冰蝎”动态二进制加密网站管理客户端](https://github.com/rebeyond/Behinder)

### 哥斯拉
[GitHub - BeichenDream/Godzilla: 哥斯拉](https://github.com/BeichenDream/Godzilla)

# 13、上线木马
### 猫猫二开CS
[GitHub - TryGOTry/CobaltStrike_Cat_4.5: 猫猫Cs:基于Cobalt Strike[4.5]二开 (原dogcs二开移植)](https://github.com/TryGOTry/CobaltStrike_Cat_4.5)



