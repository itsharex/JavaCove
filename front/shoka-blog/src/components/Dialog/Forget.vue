<template>
  <n-modal class="bg" v-model:show="dialogVisible" preset="dialog" :show-icon="false" transform-origin="center"
    :block-scroll="false">
    <n-input class="mt-11" placeholder="手机号" v-model:value="forgetForm.username"></n-input>
    <n-input-group class="mt-11">
      <n-input placeholder="验证码" v-model:value="forgetForm.code" />
      <n-button color="#49b1f5" :disabled="flag" @click="sendCode">
        {{ timer == 0 ? '发送' : `${timer}s` }}
      </n-button>
    </n-input-group>
    <n-input class="mt-11" type="password" show-password-on="click" placeholder="密码"
      v-model:value="forgetForm.password"></n-input>
    <n-button class="mt-11" color="#4caf50" style="width:100%" @click="handleForget" :loading="loading">
      确定
    </n-button>
    <div class="mt-10"><span class="dialog-text">已有账号？</span><span class="colorFlag" @click="handleLogin">登录</span>
    </div>
  </n-modal>
</template>

<script setup lang="ts">
import { getCode } from "@/api/login";
import { updateUserPassword } from "@/api/user";
import { UserForm } from "@/model";
import useStore from "@/store";
import { useIntervalFn } from "@vueuse/core";
const { app } = useStore();
const data = reactive({
  timer: 0,
  flag: false,
  loading: false,
  forgetForm: {
    username: "",
    password: "",
    code: "",
  } as UserForm,
});
const { timer, flag, loading, forgetForm } = toRefs(data);
const { pause, resume } = useIntervalFn(() => {
  timer.value--;
  if (timer.value <= 0) {
    // 停止定时器
    pause();
    flag.value = false;
  }
}, 1000, { immediate: false });
const start = (time: number) => {
  flag.value = true;
  timer.value = time;
  // 启动定时器
  resume();
};
const sendCode = () => {
  let reg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
  if (!reg.test(forgetForm.value.username)) {
    window.$message?.warning("手机号格式不正确");
    return;
  }
  start(60);
  getCode(forgetForm.value.username).then(({ data }) => {
    if (data.flag) {
      window.$message?.success("发送成功");
    }
  });
};
const handleForget = () => {
  if (forgetForm.value.code.trim().length != 6) {
    window.$message?.warning("请输入6位验证码");
    return;
  }
  if (forgetForm.value.password.trim().length < 6) {
    window.$message?.warning("密码不能少于6位");
    return;
  }
  loading.value = true;
  updateUserPassword(forgetForm.value).then(({ data }) => {
    if (data.flag) {
      window.$message?.success("修改成功");
      forgetForm.value = {
        username: "",
        password: "",
        code: "",
      }
      app.setForgetFlag(false);
    }
    loading.value = false;
  });
};
const dialogVisible = computed({
  get: () => app.forgetFlag,
  set: (value) => app.forgetFlag = value,
});
const handleLogin = () => {
  app.setForgetFlag(false);
  app.setLoginFlag(true);
};
</script>

<style scoped></style>
