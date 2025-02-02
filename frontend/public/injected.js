const observer = new MutationObserver((mutationsList) => {
    mutationsList.forEach((mutation) => {
        if (mutation.type !== 'childList') return;
        mutation.addedNodes.forEach((node) => {
            if (!(node.nodeType instanceof Element)) return;
            /**
             * @type {Element}
             */
            const element = node;

            if (!(element.tagName !== 'script')) return;
            /**
             * @type {HTMLScriptElement}
             */
            const script = node;

            if (script.src === '') return;

            script.src = `${location.origin}/proxy?url=${script.src}`;
        });
    });
});

observer.observe(document.body, {
    childList: true,
    subtree: true,
});
