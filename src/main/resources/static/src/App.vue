<template>
  <div id="app">
    <div class="logo">
      <img src="http://zhouql.vip/images/logo.svg" draggable="false" />
    </div>
    <div class="input_search">
      <input
        type="text"
        placeholder="请输入视频链接..."
        @input="change"
        @keyup.enter="download"
        v-model.trim="videoUrl"
      />
      <button @click="download">获取</button>
    </div>
    <div class="use-info">
      <el-collapse :accordion="true" v-model="activeNames">
        <el-collapse-item v-show="downloadUrl != '' && videoUrl != ''" title="视频地址" name="1">
          <p>{{downloadUrl}}</p>
          <p>
            <a :href="downloadUrl" target="_blank">预览视频</a>
            <a href @click.prevent="downVideo">下载视频</a>
            <el-progress v-show="downVideoStatus == true" :percentage="progress"></el-progress>
          </p>
        </el-collapse-item>
        <el-collapse-item v-show="bgUrl != '' && videoUrl != ''" title="封面地址" name="2">
          <p>{{imgUrl}}</p>
          <p>
            <a rel="noreferrer" :href="imgUrl" target="_blank">预览图片</a>
          </p>
        </el-collapse-item>
        <el-collapse-item title="使用说明" name="3">
          <p>1. 手机，复制视频链接即可</p>
          <p>2. 手机分集视频，分享在浏览器打开，再复制地址栏链接下载</p>
          <p>3. 电脑，复制地址栏地址即可</p>
          <p>4. 如果不行就是作者不允许下载</p>
          <p>5. 图片可以长按进行保存</p>
          <p>6. 电脑可用Ctrl+B快速清空输入</p>
          <p>
            7. 作者很棒，
            <a href="https://space.bilibili.com/1608325226" target="_blank">关注作者</a>
          </p>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="tip">
      仅供学习使用，
      <a href="https://icp.chinaz.com/zhouql.vip" target="_blank">豫备案号：zhouql.vip</a>
    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "App",
  mounted() {
    // 添加全局ctrl + b清空输入
    document.body.addEventListener("keydown", e => {
      if (e.ctrlKey && e.keyCode == 66) {
        this.videoUrl = "";
      }
    });
    setTimeout(() => {
      this.$notify({
        title: "欢迎使用",
        message: "欢迎使用本工具，愿你有个好心情✨",
        type: "success",
        duration: 2500
      });
    }, 500);
    document.querySelector("input").focus();
    setTimeout(() => {
      console.clear();
      this.printInfo();
    }, 100);
    setInterval(() => {
      console.clear();
      this.printInfo();
    }, 1000 * 60);
  },
  data() {
    return {
      activeNames: "",
      // 视频url
      videoUrl: "",
      // 封面地址
      bgUrl: "",
      // 当前视频名
      videoName: "",
      // bv
      bv: "",
      // avid
      avid: "",
      // cid
      cid: "",
      // p，分集视频
      p: "",
      // 视频下载url
      downloadUrl: "",
      // 请求加载动画
      loading: 0,
      // 视频下载进度
      progress: 0,
      // 视频下载状态
      downVideoStatus: false,
      // 版本信息
      info: {
        version: "1.5",
        author: "王子周棋洛✨",
        name: "BiliBili视频下载",
        desc:
          "此版本对新增下载进度，对下载视频命名进行处理，下载视频名即哔哩哔哩视频标题，同时进行了许多逻辑和ui优化",
        link: "http://zhouql.vip/bilibili"
      }
    };
  },
  methods: {
    cancel() {},
    // 下载视频
    downVideo() {
      let _this = this;
      _this.loading = 0;
      const CancelToken = axios.CancelToken;
      _this.$notify.closeAll();
      _this.$notify({
        title: "下载中",
        message: "视频正在下载中，请等待进度条...",
        iconClass: "el-icon-loading",
        duration: _this.loading
      });
      this.downVideoStatus = true;
      axios
        .get(`${_this.downloadUrl}`, {
          responseType: "blob",
          cancelToken: new CancelToken(function executor(c) {
            // executor 函数接收一个 cancel 函数作为参数
            _this.cancel = c;
          }),
          onDownloadProgress: evt => {
            this.progress = parseInt((evt.loaded / evt.total) * 100);
          }
        })
        .then(resp => {
          _this.loading = 5;
          let blobUrl = window.URL.createObjectURL(resp.data);
          let a = document.createElement("a");
          a.download = `${_this.videoName}` + ".mp4";
          a.href = blobUrl;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(blobUrl);
          _this.$notify.closeAll();
          setTimeout(() => {
            _this.$notify({
              title: "下载成功",
              message: "视频下载成功！",
              type: "success",
              duration: 2000
            });
          }, 1000);
        });
    },
    // 百分比格式化
    format(percentage) {
      return percentage === 100 ? "满" : `${percentage}%`;
    },
    // 预览图片
    openImg() {
      window.open(this.imgUrl);
    },
    // 输入变化，重置数据
    change() {
      // 取消之前的异步请求
      this.cancel();
      if (this.bgUrl != "") {
        this.bgUrl = "";
        this.progress = 0;
        this.downVideoStatus = false;
      }
      if (this.downloadUrl != "") {
        this.downloadUrl = "";
        this.progress = 0;
        this.downVideoStatus = false;
      }
    },
    // 下载
    download() {
      this.change();
      // 解析url，提取bv
      this.parseUrl();
      // 如果解析成功，即bv不等于空
      if (this.bv != "") {
        // 获取av，cv，然后去请求下载地址
        this.getAvidCidByBv();
      }
    },
    // 解析url，提取bv，同时如果有p，提取p
    parseUrl() {
      this.loading = 0;
      let _this = this;
      if (_this.videoUrl != "") {
        // 如果当前视频分片，获取p和bv
        if (this.videoUrl.includes("?p=")) {
          try {
            // 获取当前p
            this.p = this.videoUrl.split("?p=")[1].split("&")[0];
            // 当前视频bv
            this.bv = this.videoUrl.split("/video/")[1].split("?p=")[0];
          } catch {
            this.$notify.closeAll();
            this.$notify({
              title: "bv获取失败",
              message: "很糟糕，bv获取失败了！请检查链接",
              type: "error",
              duration: 2000
            });
            return "";
          }
          //手机链接
        } else if (this.videoUrl.includes("https://b23.tv")) {
          // 手机端链接没有去除文字
          if (this.videoUrl.startsWith("【")) {
            this.videoUrl = this.videoUrl.split("】 ")[1];
          }
          // 手机端链接已经去除文字，不做处理
          this.$notify.closeAll();
          this.$notify({
            title: "解析中",
            message: "正在解析手机端链接...",
            duration: this.loading,
            iconClass: "el-icon-loading"
          });
          //请求获取响应内容
          axios
            .get("/move?url=" + this.videoUrl)
            .then(resp => {
              console.log(resp);
              if (resp.data != "") {
                let h5 = resp.data;
                let h5Bv = h5
                  .split(
                    `<meta data-vue-meta="true" itemprop="url" content="`
                  )[1]
                  .split(`/">`)[0];
                this.videoUrl = h5Bv;
                // 解析成功，正在请求消失
                _this.loading = 5;
                // 成功提示
                this.$notify.closeAll();
                this.$notify({
                  title: "解析成功",
                  message: "手机端链接成功，准备解析下载地址...",
                  type: "success",
                  duration: 1500
                });
                setTimeout(function() {
                  // 调用下载方法
                  _this.download();
                }, 1500);
              } else {
                this.$notify.closeAll();
                this.$notify({
                  title: "解析失败",
                  message: "很糟糕，解析失败了...",
                  type: "error",
                  duration: 2000
                });
              }
            });
          // 普通视频，只用获取bv
        } else {
          try {
            // 当前视频bv
            this.bv = this.videoUrl
              .split("/video/")[1]
              .split("/?spm_id_from")[0];
          } catch {
            this.$notify.closeAll();
            this.$notify({
              title: "bv获取失败",
              message: "很糟糕，bv获取失败了！请检查链接",
              type: "error",
              duration: 2000
            });
            return "";
          }
        }
      } else {
        this.$notify.closeAll();
        this.$notify({
          title: "错误提示",
          message: "链接不能为空！请检查链接后重试",
          type: "error",
          duration: 2000
        });
      }
    },
    // 通过bv获取avid和cid
    getAvidCidByBv() {
      this.loading = 0;
      this.bgUrl = "";
      this.downloadUrl = "";
      this.$notify.closeAll();
      this.$notify({
        title: "请求中",
        message: "正在请求avid和cid...",
        duration: this.loading,
        iconClass: "el-icon-loading"
      });
      axios.get(`/av/${this.bv}`).then(resp => {
        // this.vue
        let _this = this;
        if (resp.data.code == 0) {
          // 获取视频标题
          _this.videoName = resp.data.data.title;
          // avid不需要特殊处理
          _this.avid = resp.data.data.aid;
          // 获取封面
          _this.bgUrl = resp.data.data.pic;
          // 如果分段p不等于空，就有多个cid，找出当前p的cid
          if (_this.p != "") {
            try {
              // 暂存一下pages数组
              let pages = resp.data.data.pages;
              // 获取cid
              _this.cid = pages[_this.p - 1].cid;
            } catch {
              _this.$notify.closeAll();
              _this.$notify.error({
                title: "错误",
                message: "bv解析错误，请检查链接...",
                duration: 2000
              });
              return;
            }
            // 如果单个视频，直接返回pages[0]的cid
          } else {
            _this.cid = resp.data.data.pages[0].cid;
          }
          // 解析成功，正在请求消失
          _this.loading = 5;
          // 使用acid和cid获取视频地址
          _this.getDownloadUrl();
        } else if (resp.data.code == "-400") {
          _this.$notify.closeAll();
          _this.$notify.error({
            title: "错误",
            message: "bv解析错误，请检查链接...",
            duration: 2000
          });
          return;
        }
      });
    },
    // 拼接地址，请求获取下载链接
    getDownloadUrl() {
      axios
        .get(`/download/${this.avid}/${this.cid}`)
        .then(resp => {
          this.downloadUrl = resp.data.data.durl[0].url;
          if (this.downloadUrl != "") {
            this.$notify.closeAll();
            this.$notify({
              title: "成功",
              message: "视频和封面请求成功，快去下载吧",
              type: "success",
              duration: 2500
            });
            setTimeout(() => {
              this.bv = "";
              this.p = "";
              this.avid = "";
              this.cid = "";
            }, 1900);
          }
        });
    },
    // 打印版本信息
    printInfo() {
      console.log(`version: ${this.info.version}`);
      console.table(`author :${this.info.author}`);
      console.log(`name   : ${this.info.name}`);
      console.log(`desc   : ${this.info.desc}`);
      console.log(`link   : ${this.info.link}`);
    }
  },
  computed: {
    imgUrl() {
      return "https" + this.bgUrl.substring(4);
    }
  }
};
</script>

<style lang="less">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.content {
  padding: 16px 16px 160px;
}

#app {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;
  min-height: 100vh;
  .logo {
    width: 35%;
    display: flex;
    justify-content: center;
    align-items: center;
    img {
      width: 30%;
      margin-top: 200px;
      user-select: none;
    }
  }
  .input_search {
    width: 45%;
    margin-top: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    input {
      width: 85%;
      height: 45px;
      padding-right: 20px;
      border: 1px solid #888;
      border-right: none;
      border-radius: 7px 0 0 7px;
      font-size: 16px;
      color: #363636;
      outline: none;
      text-indent: 0.7em;
      &::placeholder {
        color: #979797;
        font-size: 14px;
      }
    }
    button {
      width: 15%;
      height: 45px;
      border: none;
      background-color: #1296db;
      border-radius: 0 7px 7px 0;
      font-size: 14px;
      cursor: pointer;
      position: relative;
      letter-spacing: 0.1em;
      color: #fff;
      padding: 2px 4px;
      outline: none;
      user-select: none;
    }
  }
  .use-info {
    width: 44.5%;
    user-select: none;
  }
  .tip {
    position: fixed;
    bottom: 0px;
    left: 50%;
    width: 100%;
    height: 30px;
    background-color: #fefefeed;
    text-align: center;
    transform: translateX(-50%);
    font-size: 12px;
    color: #949494;
    display: flex;
    align-items: center;
    justify-content: center;
    a {
      text-decoration: none;
      color: #1296db;
    }
  }
  .van-cell::after {
    border: none;
  }
  @media screen and (max-width: 600px) {
    .logo {
      width: 55%;
      img {
        width: 50%;
      }
    }
    .input_search {
      width: 90%;
      input {
        width: 85%;
        height: 40px;
      }
      button {
        width: 15%;
        height: 40px;
      }
    }
    .use-info {
      width: 89%;
    }
  }
  .el-collapse-item__header {
    border: none;
  }
  .el-collapse-item__content {
    padding-bottom: 7px;
    p {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      color: #888;
      padding: 0 0 10px 0;
      a {
        display: inline-block;
        background-color: #eee;
        color: #1296db;
        font-weight: 700;
        padding: 0px 10px;
        border-radius: 100px;
        margin-right: 20px;
        height: 21px;
        line-height: 21px;
        font-size: 12px;
        text-decoration: none;
        transition: background-color 0.3s, color 0.1s;
        &:hover {
          background-color: #1296db;
          color: #fcfcfc;
        }
      }
    }
  }
  .tablist{
    margin-bottom: 100px;
  }
  .el-progress-bar__inner {
    background-color: #1296db;
  }
  .el-progress-bar {
    padding-top: 12px;
  }
  .el-progress__text {
    margin-left: 12px;
  }
}
</style>
