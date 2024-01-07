export function extractMinutes(duration: string) {
  const hIdx = duration.indexOf('H');
  const minutes = duration.substring(hIdx + 1, duration.length - 1);

  return minutes === '0' ? '' : minutes;
}

export function extractHours(duration: string) {
  const hIdx = duration.indexOf('H');
  const hours = duration.substring(2, hIdx);

  return hours === '0' ? '' : hours;
}

export function mapToCorrectFormat(duration: string) {
  const hIdx = duration.indexOf('H');
  const mIdx = duration.indexOf('M');

  // Can always ever be one of either as a minute or hour has to be present.
  if (hIdx === -1) {
    const minutes = extractMinutes(duration);
    return 'PT0H' + minutes + 'M';
  }

  if (mIdx === -1) {
    const hours = extractHours(duration);
    return 'PT' + hours + 'H0M';
  }

  return duration;
}
