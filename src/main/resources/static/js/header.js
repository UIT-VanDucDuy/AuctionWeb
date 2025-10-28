
document.addEventListener('DOMContentLoaded', function() {
    function updateDateTime() {
        const now = new Date();
        const day = now.getDate().toString().padStart(2, '0');
        const month = (now.getMonth() + 1).toString().padStart(2, '0');
        const year = now.getFullYear();
        const hours = now.getHours().toString().padStart(2, '0');
        const minutes = now.getMinutes().toString().padStart(2, '0');
        const seconds = now.getSeconds().toString().padStart(2, '0');
        document.getElementById("current_date").textContent =
            `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
    }

    updateDateTime();
    setInterval(updateDateTime, 1000);


    // 2. Xử lý Fixed Nav khi cuộn
    const mainNavBar = document.getElementById('mainNavBar');
    const navPlaceholder = document.getElementById('navPlaceholder');
    const topBar = document.querySelector('.top-bar');
    const logoSearch = document.querySelector('.logo-search-section'); // Đã thêm class cho section này

    if (mainNavBar && navPlaceholder && topBar && logoSearch) {

        let headerSectionsHeight = 0;

        const calculateTriggerHeight = () => {
            // Tính tổng chiều cao của Top Bar và Logo/Search Section
            headerSectionsHeight = topBar.offsetHeight + logoSearch.offsetHeight;
        };

        const setFixedNav = () => {
            const scrollPosition = window.scrollY;
            const navHeight = mainNavBar.offsetHeight;

            if (scrollPosition >= headerSectionsHeight) {
                // Nếu đã cuộn qua phần header trên, áp dụng Fixed
                if (!mainNavBar.classList.contains('is-fixed')) {
                    mainNavBar.classList.add('is-fixed');
                    // Tạo placeholder để nội dung không bị nhảy
                    navPlaceholder.style.height = navHeight + 'px';
                    navPlaceholder.style.display = 'block';
                }
            } else {
                // Nếu cuộn lên trở lại, loại bỏ Fixed
                if (mainNavBar.classList.contains('is-fixed')) {
                    mainNavBar.classList.remove('is-fixed');
                    navPlaceholder.style.display = 'none';
                    navPlaceholder.style.height = '0';
                }
            }
        };

        const init = () => {
            calculateTriggerHeight();
            setFixedNav();
        };

        // Gắn sự kiện cuộn, tải trang, và thay đổi kích thước
        window.addEventListener('scroll', setFixedNav);
        window.addEventListener('load', init);
        window.addEventListener('resize', init);

        // Lần gọi đầu tiên (dùng setTimeout để đảm bảo render)
        setTimeout(init, 100);
    }
    // 3. Xử lý Dropdown thông báo
    const notificationToggle = document.getElementById('notificationToggle');
    const notificationMenu = document.getElementById('notificationMenu');
    const userMenu = document.querySelector('.custom-dropdown-menu'); // Lấy menu người dùng (dùng hover)

    if (notificationToggle && notificationMenu) {
        notificationToggle.addEventListener('click', (e) => {
            e.stopPropagation(); // Ngăn sự kiện click lan truyền

            // Đóng menu người dùng (Dù nó dùng hover, click có thể gây xung đột)
            // Tuy nhiên, vì menu người dùng dùng CSS hover, ta chỉ cần toggle menu thông báo:
            notificationMenu.classList.toggle('show');
        });

        // Đóng menu khi click bên ngoài
        document.addEventListener('click', (e) => {
            // Kiểm tra xem click có phải bên trong menu thông báo hoặc nút toggle không
            if (notificationMenu.classList.contains('show') &&
                !notificationMenu.contains(e.target) &&
                e.target !== notificationToggle &&
                !notificationToggle.contains(e.target)) {

                notificationMenu.classList.remove('show');
            }

            // Giữ nguyên logic hover cho menu người dùng
        });
    }
});
