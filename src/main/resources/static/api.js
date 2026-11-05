/* Same-origin server API. Credentials stay on the server. */
const API = (() => {
  async function request(path, body) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 40000);
    try {
      const response = await fetch(`/api${path}`, {
        method: body === undefined ? 'GET' : 'POST',
        credentials: 'same-origin',
        signal: controller.signal,
        headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
        body: body === undefined ? undefined : JSON.stringify(body),
      });
      const data = await response.json();
      if (!response.ok) {
        const error = new Error(data.message || '요청을 처리하지 못했어요. 다시 시도해주세요.');
        error.code = data.code;
        throw error;
      }
      return data;
    } catch (error) {
      if (error.name === 'AbortError') throw new Error('응답이 늦어지고 있어요. 잠시 후 다시 시도해주세요.');
      if (error instanceof TypeError) throw new Error('서버에 연결할 수 없어요. 연결 상태를 확인해주세요.');
      throw error;
    } finally { clearTimeout(timer); }
  }
  return {
    bootstrap: () => request('/bootstrap'),
    preference: body => request('/preferences', body),
    recommend: preferenceId => request('/recommendations', { preferenceId }),
    review: body => request('/letters/review', body),
    save: body => request('/letters', body),
    letter: id => request(`/letters/${encodeURIComponent(id)}`),
  };
})();
