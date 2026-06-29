$(function () {
	// 고등학교 검색
	const searchSchool = function () {
		const btnSchool = $('.btn-school');
		const btnSchoolClose = $('.btn-layer-close')
		const layrerSchool = $('.func-layer');

		btnSchool.on('click', function () {
			if (!layrerSchool.hasClass('on')) {
				layrerSchool.addClass('on');
			} else {
				layrerSchool.removeClass('on');
			}
		});

		btnSchoolClose.on('click', function () {
			layrerSchool.removeClass('on');
		})
	};

	searchSchool();
});