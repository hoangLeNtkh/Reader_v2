let isReady = false;
let currentPage = 0;
let totalPages = 0;
let columnWidth = 0;

const Android = {
    onStateChange: (stateJson) => {
        if (window.AndroidBridge && window.AndroidBridge.onStateChange) {
            window.AndroidBridge.onStateChange(stateJson);
        }
    },
    onEdgeTap: (direction) => {
        if (window.AndroidBridge && window.AndroidBridge.onEdgeTap) {
            window.AndroidBridge.onEdgeTap(direction);
        }
    }
};

const readyCommand = {
    func: null,
    args: []
};

function setupAndGoTo(progress, preferEnd = false) {
    setupPagination();

    const command = () => {
        if (preferEnd) {
            goToPage(totalPages - 1);
        } else {
            const targetPage = Math.floor(progress * totalPages);
            const page = Math.min(targetPage, totalPages - 1);
            goToPage(page);
        }
        updateState();
    };

    if (isReady) {
        command();
    } else {
        readyCommand.func = command;
    }
}

function setupPagination() {
    isReady = false;
    const body = document.body;
    const html = document.documentElement;

    let wrapper = document.getElementById('reader-wrapper');
    if (!wrapper) {
        wrapper = document.createElement('div');
        wrapper.id = 'reader-wrapper';
        while (body.firstChild) {
            wrapper.appendChild(body.firstChild);
        }
        body.appendChild(wrapper);
    }

    html.style.overflow = 'hidden';
    body.style.overflow = 'hidden';
    body.style.margin = '0';
    body.style.padding = '0';

    wrapper.style.height = window.innerHeight + 'px';
    wrapper.style.width = window.innerWidth + 'px';
    wrapper.style.margin = '0';
    wrapper.style.transition = 'transform 0.3s ease-out';
    const horizontalPadding = 40;
    wrapper.style.paddingLeft = (horizontalPadding / 2) + 'px';
    wrapper.style.paddingRight = (horizontalPadding / 2) + 'px';
    wrapper.style.boxSizing = 'border-box';
    columnWidth = window.innerWidth - horizontalPadding;
    wrapper.style.columnWidth = columnWidth + 'px';
    wrapper.style.columnGap = horizontalPadding + 'px';
    wrapper.style.columnFill = 'auto';

    setTimeout(() => {
            const totalContentWidth = wrapper.scrollWidth;
            const stride = columnWidth + horizontalPadding;
            totalPages = Math.max(1, Math.ceil(totalContentWidth / stride));
            console.log(`Setup complete. Total Pages: ${totalPages}`);
            isReady = true;

            if (typeof readyCommand.func === 'function') {
                readyCommand.func(...readyCommand.args);
                readyCommand.func = null;
                readyCommand.args = [];
            }

            if (currentPage >= totalPages) currentPage = totalPages - 1;
            goToPage(currentPage);
            updateState();
        }, 0);
}

function goToPage(pageNumber) {
    if (!isReady) return;

        if (pageNumber < 0) pageNumber = 0;
        if (pageNumber >= totalPages) pageNumber = totalPages - 1;

        currentPage = pageNumber;
        const wrapper = document.getElementById('reader-wrapper');
        const shift = currentPage * window.innerWidth;
        wrapper.style.transform = `translateX(-${shift}px)`;
        console.log(`Going to page ${currentPage}. Shift: -${shift}px`);
}

function goToNextPage() {
    console.log(`Next requested. Current: ${currentPage}, Total: ${totalPages}`);

    if (currentPage < totalPages - 1) {
        goToPage(currentPage + 1);
        updateState();
    } else {
        console.log("Edge tap: next");
        Android.onEdgeTap('next');
    }
}

function goToPreviousPage() {
    console.log(`Prev requested. Current: ${currentPage}`);

    if (currentPage > 0) {
        goToPage(currentPage - 1);
        updateState();
    } else {
        console.log("Edge tap: previous");
        Android.onEdgeTap('previous');
    }
}

function goToPosition(progress) {
    if (!isReady) {
        setTimeout(() => goToPosition(progress), 100);
        return;
    }
    const targetPage = Math.floor(progress * totalPages);
    const page = Math.min(targetPage, totalPages - 1);
    goToPage(page);
    updateState();
}

function updateState() {
    const state = { currentPage: currentPage, totalPages: totalPages, isReady: isReady };
    Android.onStateChange(JSON.stringify(state));
}

window.addEventListener('resize', setupPagination);
