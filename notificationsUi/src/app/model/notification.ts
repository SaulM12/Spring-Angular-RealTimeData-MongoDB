export interface Notification {
    id?: string,
    message: string,
    severity: string
}
export enum NotificationSeverities {
    INFO = 'Nueva',
    WARN = 'En progreso',
    SUCCESS = 'Resuelta',
}