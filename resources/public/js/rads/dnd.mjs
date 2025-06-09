const defaults = {
  debug: console.debug,
};

function createHandlers(options = {}) {
  const { debug, onEvent, generateId, placeholderClasses, draggableClasses } = {
    ...defaults,
    ...options,
  };

  const state = {
    isReadyToDrag: false,
    isDragging: false,
    dragElement: null,
    dragClone: null,
    startPos: { x: 0, y: 0 },
    currentPos: { x: 0, y: 0 },
    offset: { x: 0, y: 0 },
    dropTarget: null,
  };

  function cleanup() {
    if (state.dragElement) {
      state.dragElement.classList.remove(...placeholderClasses.split(" "));
    }

    if (state.dragClone) {
      state.dragClone.remove();
    }

    if (state.dropTarget) {
      state.dropTarget.classList.remove("drag-over");
    }

    document.body.style.userSelect = "";

    state.isReadyToDrag = false;
    state.isDragging = false;
    state.dragElement = null;
    state.dragClone = null;
    state.dropTarget = null;
    state.startPos = { x: 0, y: 0 };
    state.currentPos = { x: 0, y: 0 };
    state.offset = { x: 0, y: 0 };
  }

  function updateDropTarget(event) {
    const elementBelow = document.elementFromPoint(
      event.clientX,
      event.clientY
    );
    const dropTarget = elementBelow?.closest('[data-droppable="true"]');

    if (dropTarget !== state.dropTarget) {
      if (state.dropTarget) {
        state.dropTarget.classList.remove("drag-over");
      }
      if (dropTarget) {
        dropTarget.classList.add("drag-over");
      }
      state.dropTarget = dropTarget;
    }
  }

  function startDragging() {
    if (!state.isReadyToDrag || state.isDragging) return;

    debug("Drag started:", state.dragElement.dataset.id);
    state.isDragging = true;

    const element = state.dragElement;
    const rect = element.getBoundingClientRect();

    element.classList.add(...placeholderClasses.split(" "));

    // Create a clone of the element
    const clone = element.cloneNode(true);
    clone.style.position = "fixed";
    clone.style.top = `${rect.top}px`;
    clone.style.left = `${rect.left}px`;
    clone.style.width = `${rect.width}px`;
    clone.style.height = `${rect.height}px`;
    clone.style.pointerEvents = "none";
    clone.style.zIndex = "1000";
    clone.style.opacity = "1";
    clone.classList.add(...draggableClasses.split(" "));
    if (!clone.dataset.id) clone.dataset.id = generateId(element);

    document.body.appendChild(clone);
    state.dragClone = clone;
    document.body.style.userSelect = "none";
  }

  function handleDrop(event) {
    if (state.dropTarget) {
      state.dropTarget.classList.remove("drag-over");
    }
    const dropEvent = {
      type: "drop",
      dragElement: state.dragElement,
      dragClone: state.dragClone,
      dropTarget: state.dropTarget,
      dropEvent: event,
    };
    debug("Drop event triggered:", dropEvent);
    onEvent(dropEvent);
  }

  return {
    pointerdown(event) {
      const element = event.target.closest('[data-draggable="true"]');
      if (!element) return;

      debug("Ready to drag:", element.dataset.id);
      event.preventDefault();
      state.isReadyToDrag = true;
      state.dragElement = element;
      state.startPos = { x: event.clientX, y: event.clientY };
      state.currentPos = { x: event.clientX, y: event.clientY };

      const rect = element.getBoundingClientRect();
      state.offset = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top,
      };
    },

    pointermove(event) {
      if (!state.isReadyToDrag && !state.isDragging) return;

      event.preventDefault();
      state.currentPos = { x: event.clientX, y: event.clientY };

      // Check if we should start dragging based on movement threshold
      if (state.isReadyToDrag && !state.isDragging) {
        const deltaX = state.currentPos.x - state.startPos.x;
        const deltaY = state.currentPos.y - state.startPos.y;
        const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Start dragging if moved more than 3 pixels
        if (distance > 3) {
          startDragging();
        }
      }

      // If we're now dragging, move the clone
      if (state.isDragging && state.dragClone) {
        const deltaX = state.currentPos.x - state.startPos.x;
        const deltaY = state.currentPos.y - state.startPos.y;

        state.dragClone.style.transform = `translate(${deltaX}px, ${deltaY}px)`;
        updateDropTarget(event);
        onEvent({
          type: "drag",
          dropTarget: state.dropTarget,
          dragEvent: event,
        });
      }
    },

    pointerup(event) {
      if (!state.isReadyToDrag && !state.isDragging) return;

      event.preventDefault();

      if (state.isDragging) {
        // We were actually dragging, handle the drop
        debug(
          "Drag ended:",
          state.dragElement.dataset.id,
          "dropped on:",
          state.dropTarget?.dataset.id
        );
        handleDrop(event);
      } else {
        // We were ready to drag but never moved enough, treat as click
        debug("Click detected on:", state.dragElement.dataset.id);
      }

      cleanup();
    },

    pointercancel() {
      if (!state.isReadyToDrag && !state.isDragging) return;
      debug("Drag cancelled");
      cleanup();
    },

    pointerenter(event) {
      if (!state.isDragging) return;

      const dropTarget = event.target.closest('[data-droppable="true"]');
      if (dropTarget && dropTarget !== state.dropTarget) {
        debug("Entered drop zone:", dropTarget.dataset.id);
        if (state.dropTarget) {
          state.dropTarget.classList.remove("drag-over");
        }
        dropTarget.classList.add("drag-over");
        state.dropTarget = dropTarget;
      }
    },

    pointerleave(event) {
      if (!state.isDragging) return;

      const dropTarget = event.target.closest('[data-droppable="true"]');
      if (dropTarget === state.dropTarget) {
        debug("Left drop zone:", dropTarget.dataset.id);
        dropTarget.classList.remove("drag-over");
        state.dropTarget = null;
      }
    },
  };
}

export function start(options) {
  const handlers = createHandlers(options);

  document.addEventListener("pointerdown", handlers.pointerdown);
  document.addEventListener("pointermove", handlers.pointermove);
  document.addEventListener("pointerup", handlers.pointerup);
  document.addEventListener("pointercancel", handlers.pointercancel);
  document.addEventListener("pointerenter", handlers.pointerenter);
  document.addEventListener("pointerleave", handlers.pointerleave);

  return { handlers };
}

export function stop(dnd) {
  const { handlers } = dnd;

  document.removeEventListener("pointerdown", handlers.pointerdown);
  document.removeEventListener("pointermove", handlers.pointermove);
  document.removeEventListener("pointerup", handlers.pointerup);
  document.removeEventListener("pointercancel", handlers.pointercancel);
  document.removeEventListener("pointerenter", handlers.pointerenter);
  document.removeEventListener("pointerleave", handlers.pointerleave);
}

export function dropIndicator(event) {
  if (!event.dropTarget) return;

  const children = Array.from(
    event.dropTarget.querySelectorAll('[data-draggable="true"]')
  ).map((r) => ({ id: r.dataset.id, rect: r.getBoundingClientRect() }));

  const { clientY } = (event.dragEvent || event.dropEvent) ?? {};
  if (!clientY) return;

  const child = children.find((c) => clientY <= c.rect.bottom);
  if (!child) return;

  const middle = child.rect.top + child.rect.height / 2;
  const position = clientY < middle ? "before" : "after";

  return { id: child.id, child, position };
}
