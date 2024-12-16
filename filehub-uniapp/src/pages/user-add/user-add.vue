<template>
	<view style="width: 100%;display: flex;justify-content: center;">
		
		<view style="margin-bottom: 8px;">
			<uni-title type="h1" title="新增用户密钥"></uni-title>
		</view>
		
	</view>
	<view>
		<!-- AccessKey 部分 -->
		<uni-section title="添加AccessKey" type="line">
			<uni-card title="AccessKey" extra="数字+英文+下划线">
				<uni-easyinput v-model="accessKey" placeholder="请输入AccessKey" />
			</uni-card>
		</uni-section>
		
		<!-- AvailableSpace 部分 -->
		<uni-section :title="'设置可用空间 : ' + selectedAvailableSpaceValue + ' KB'" type="line">
			<view style="
				padding: 16px;
			">
				<uni-data-select v-model="availableSpaceValue" :localdata="availableSpaceRange"
					@change="handleCelectedChange">
				</uni-data-select>
			</view>
		</uni-section>

		<!-- ValidTime 部分 -->
		<uni-section :title="'设置密钥有效期 : '+ validTime" type="line" padding>
			<uni-card title="ValidTime" extra="有效时间 (分钟)">
				<uni-number-box :min="-1" :max="43200" v-model="validTime" />
			</uni-card>
		</uni-section>
	</view>
	<view style="display: flex;justify-content: center;text-align: center;">
		<view style="
				margin-left: auto;
				margin-right: auto;
				margin-top: 18px;
				width: 48%;
				padding: 8px;
				background-color: rgb(141, 84,255);
				border-radius: 8px;
				box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1), 0 3px 10px rgba(0, 0, 0, 0.19);
			"
			@click="handleCommit"
		>
			<text style="font-weight: 600;color: aliceblue; letter-spacing: 2px;">确认添加🪄</text>
		</view>
	</view>
</template>

<script setup lang="ts">
	import {
		ref,
		onMounted
	} from 'vue';
import type { AddUserParams } from '@/api/types/type';
import { addUserService } from '@/api/service/user';

	// 定义所有的数据属性
	const userParams = ref({
		accessKey: 'a',
		validTime: 0,
		availableSpace: 0
	})
	const validTime = ref(100);
	const accessKey = ref('')
	const buttonLoadingVisible = ref(false)
	const selectedAvailableSpaceValue = ref(524288)
	const availableSpaceValue = ref(2); // 默认选择 "512MB"
	const availableSpaceRange = ref([
		{
			value: 0,
			text: '1GB'
		}, // 1048576KB
		{
			value: 1,
			text: '2GB'
		}, // 2097152KB
		{
			value: 2,
			text: '512MB'
		}, // 524288KB
		{
			value: 3,
			text: '256MB'
		}, // 262144KB
		{
			value: 4,
			text: '128MB'
		}, // 131072KB
		{
			value: 5,
			text: '64MB'
		},
		{
			value: 6,
			text: '32MB'
		}
	]);

	const handleCelectedChange = (e : number) => {
		switch (e) {
			case 0:
				selectedAvailableSpaceValue.value = 1048576; // 1GB in KB
				break;
			case 1:
				selectedAvailableSpaceValue.value = 2097152; // 2GB in KB
				break;
			case 2:
				selectedAvailableSpaceValue.value = 524288; // 512MB in KB
				break;
			case 3:
				selectedAvailableSpaceValue.value = 262144; // 256MB in KB
				break;
			case 4:
				selectedAvailableSpaceValue.value = 131072; // 128MB in KB
				break;
			case 5:
				selectedAvailableSpaceValue.value = 65536; // 64MB in KB
				break;
			case 6:
				selectedAvailableSpaceValue.value = 32768; // 32MB in KB
				break;
			default:
				console.log('Unexpected value:', e);
				break;
		}
	};
	
	const handleCommit = async () => {
		
		uni.showModal({
			title: '确认添加？',
			content: accessKey.value,
			success: async function (res) {
				if (res.confirm) {
					if (accessKey.value === '' || !accessKey.value ) {
						uni.showToast({
							icon: 'error',
							title: '请输入AccessKey!',
							duration: 2000
						});
						return
					}
					console.log('用户点击确定');
					uni.showLoading({
						title: '正在添加' + accessKey.value
					})
					userParams.value.accessKey = accessKey.value
					userParams.value.availableSpace = selectedAvailableSpaceValue.value
					userParams.value.validTime = validTime.value
					let result = await addUserService(userParams.value)
					uni.hideLoading()
					uni.showToast({
						icon:'success',
						title: result.message,
						duration: 2000
					});
				} else if (res.cancel) {
					console.log('用户点击取消');
				}
			}
		});
	}
</script>

<style scoped>

</style>